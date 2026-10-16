package com.jeremykenedy.pipeworksdream;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

final class PipeworksSceneView extends GLSurfaceView {
  private boolean running;
  private final SceneRenderer renderer;
  private final Runnable frame =
      new Runnable() {
        @Override
        public void run() {
          if (running) {
            requestRender();
            postDelayed(this, 33L);
          }
        }
      };

  PipeworksSceneView(Context context) {
    super(context);
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    PipeworksOptions options =
        PipeworksOptions.resolve(
            preferences.getString("density", "handful"), preferences.getString("speed", "natural"),
            preferences.getString("palette", "copper"),
                preferences.getString("background", "midnight"),
            preferences.getString("lighting", "cinematic"), preferences.getString("glow", "on"),
            preferences.getString("shape", "round"), preferences.getString("size", "medium"),
            preferences.getString("tail", "keep"), preferences.getString("tail_delay", "15"),
            preferences.getBoolean("randomize_all", false), new Random());
    setEGLContextClientVersion(2);
    setEGLConfigChooser(8, 8, 8, 0, 16, 0);
    setPreserveEGLContextOnPause(true);
    renderer = new SceneRenderer(options);
    setRenderer(renderer);
    setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
  }

  void start() {
    if (running) return;
    running = true;
    renderer.startWorker();
    onResume();
    post(frame);
  }

  void stop() {
    if (!running) return;
    running = false;
    removeCallbacks(frame);
    renderer.stopWorker();
    onPause();
  }

  private static final class SceneRenderer implements GLSurfaceView.Renderer {
    private static final int[][] PALETTES = {
      {
        0xffc87939,
        0xffe4a45b,
        0xffa45a32,
        0xffefbc71,
        0xffbf6944,
        0xffd29554,
        0xff98704c,
        0xffd8a26b
      },
      {
        0xff72b6ce,
        0xff85c6b8,
        0xff638ebc,
        0xffb3c6d5,
        0xff5597b3,
        0xff93b0bd,
        0xff8badce,
        0xff72bcaf
      },
      {
        0xffc642e1,
        0xff28d4cb,
        0xff4ac76d,
        0xffe7b83c,
        0xffdd4d68,
        0xff608dda,
        0xffa4d14d,
        0xffdc7843
      },
      {
        0xffc9d4db,
        0xff899ca8,
        0xffe0e4e5,
        0xff718695,
        0xffb3bfc6,
        0xff91a4b1,
        0xffdadfdf,
        0xff7f939f
      },
      {0xffdb4040},
      {0xff41b86d},
      {0xff448cdb},
      {0xff9d55d4},
      {0xffe8b642}
    };
    private final PipeworksOptions options;
    private final float[] projection = new float[16];
    private final float[] view = new float[16];
    private final float[] matrix = new float[16];
    private final FloatBuffer screen =
        buffer(new float[] {-1, -1, 1, -1, -1, 1, -1, 1, 1, -1, 1, 1});
    private PipeEngine engine;
    private volatile ExecutorService worker;
    private volatile PipeMesh prepared;
    private volatile boolean preparing;
    private volatile int epoch;
    private FloatBuffer mesh;
    private int vertexCount;
    private int meshBuffer;
    private int pipeProgram;
    private int backgroundProgram;
    private long stepAt;
    private long startedAt;
    private int width;
    private int height;

    SceneRenderer(PipeworksOptions options) {
      this.options = options;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
      pipeProgram = program(PIPE_VERTEX, PIPE_FRAGMENT);
      backgroundProgram = program(BACKGROUND_VERTEX, BACKGROUND_FRAGMENT);
      GLES20.glEnable(GLES20.GL_DEPTH_TEST);
      GLES20.glDisable(GLES20.GL_CULL_FACE);
      int[] buffers = new int[1];
      GLES20.glGenBuffers(1, buffers, 0);
      meshBuffer = buffers[0];
      engine = new PipeEngine(17, 10, 7, options.pipeCount, new Random(), options.tailPoints());
      for (int i = 0; i < (options.followingTail ? 1 : 20); i++) engine.advance();
      synchronized (this) {
        epoch++;
        prepared = null;
        preparing = false;
      }
      upload(
          PipeMesh.build(
              engine.snapshot(),
              PALETTES[options.palette],
              options.shape,
              options.radius,
              options.tailPoints()));
      startedAt = SystemClock.uptimeMillis();
      stepAt = startedAt;
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
      this.width = width;
      this.height = Math.max(1, height);
      GLES20.glViewport(0, 0, width, height);
      Matrix.perspectiveM(projection, 0, 39f, width / (float) this.height, 0.5f, 75f);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
      long now = SystemClock.uptimeMillis();
      PipeMesh next = prepared;
      if (now - stepAt >= options.stepDelay && next != null) {
        upload(next);
        prepared = null;
        stepAt = now;
      }
      prepareNext();
      float time = (now - startedAt) * 0.001f;
      GLES20.glClearColor(0.01f, 0.015f, 0.025f, 1);
      GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
      drawBackground(time);
      // Bounded drift keeps the entire network inside the view, including its nearest corners.
      float eyeX = (float) Math.sin(time * 0.027f) * 4.5f;
      float eyeY = 2.5f + (float) Math.sin(time * 0.019f) * 1.4f;
      float eyeZ = 24f + (float) Math.cos(time * 0.023f) * 1.2f;
      Matrix.setLookAtM(view, 0, eyeX, eyeY, eyeZ, 0, 0, 0, 0, 1, 0);
      Matrix.multiplyMM(matrix, 0, projection, 0, view, 0);
      GLES20.glEnable(GLES20.GL_DEPTH_TEST);
      GLES20.glUseProgram(pipeProgram);
      GLES20.glUniformMatrix4fv(
          GLES20.glGetUniformLocation(pipeProgram, "uMatrix"), 1, false, matrix, 0);
      GLES20.glUniform3f(GLES20.glGetUniformLocation(pipeProgram, "uEye"), eyeX, eyeY, eyeZ);
      GLES20.glUniform1f(
          GLES20.glGetUniformLocation(pipeProgram, "uLight"),
          options.lighting == 0 ? .65f : options.lighting == 2 ? 1.25f : 1f);
      GLES20.glUniform1f(GLES20.glGetUniformLocation(pipeProgram, "uGlow"), options.glow ? 1f : 0f);
      GLES20.glUniform1f(
          GLES20.glGetUniformLocation(pipeProgram, "uGrowth"),
          Math.min(1f, (now - stepAt) / (float) options.stepDelay));
      meshAttribute("aPosition", 0, 3);
      meshAttribute("aNormal", 3, 3);
      meshAttribute("aColor", 6, 3);
      meshAttribute("aGrowth", 9, 1);
      meshAttribute("aTail", 10, 1);
      GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
      GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    synchronized void startWorker() {
      epoch++;
      preparing = false;
      prepared = null;
      worker = Executors.newSingleThreadExecutor();
    }

    synchronized void stopWorker() {
      epoch++;
      ExecutorService active = worker;
      worker = null;
      if (active != null) active.shutdownNow();
      prepared = null;
      preparing = false;
    }

    private void prepareNext() {
      ExecutorService active = worker;
      if (active == null || preparing || prepared != null) return;
      final int generation = epoch;
      final PipeEngine activeEngine = engine;
      preparing = true;
      try {
        active.execute(
            new Runnable() {
              @Override
              public void run() {
                int[] snapshot;
                synchronized (activeEngine) {
                  if (epoch != generation) return;
                  activeEngine.advance();
                  snapshot = activeEngine.snapshot();
                }
                PipeMesh next =
                    PipeMesh.build(
                        snapshot,
                        PALETTES[options.palette],
                        options.shape,
                        options.radius,
                        options.tailPoints());
                synchronized (SceneRenderer.this) {
                  if (epoch == generation) {
                    prepared = next;
                    preparing = false;
                  }
                }
              }
            });
      } catch (RejectedExecutionException stopped) {
        // The UI can pause the surface between checking and submitting the task.
        if (epoch == generation) preparing = false;
      }
    }

    private void upload(PipeMesh geometry) {
      if (mesh == null || mesh.capacity() < geometry.vertices.length) {
        mesh =
            ByteBuffer.allocateDirect(geometry.vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
      }
      mesh.position(0);
      mesh.put(geometry.vertices);
      mesh.position(0);
      GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, meshBuffer);
      GLES20.glBufferData(
          GLES20.GL_ARRAY_BUFFER, geometry.vertices.length * 4, mesh, GLES20.GL_DYNAMIC_DRAW);
      GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
      vertexCount = geometry.vertexCount;
    }

    private void drawBackground(float time) {
      GLES20.glDisable(GLES20.GL_DEPTH_TEST);
      GLES20.glUseProgram(backgroundProgram);
      GLES20.glUniform2f(
          GLES20.glGetUniformLocation(backgroundProgram, "uResolution"), width, height);
      GLES20.glUniform1f(GLES20.glGetUniformLocation(backgroundProgram, "uTime"), time);
      GLES20.glUniform1f(
          GLES20.glGetUniformLocation(backgroundProgram, "uStyle"), options.background);
      attribute(backgroundProgram, "aPosition", screen, 0, 2, 2);
      GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }

    private void meshAttribute(String name, int offset, int size) {
      GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, meshBuffer);
      int location = GLES20.glGetAttribLocation(pipeProgram, name);
      GLES20.glEnableVertexAttribArray(location);
      GLES20.glVertexAttribPointer(
          location, size, GLES20.GL_FLOAT, false, PipeMesh.FLOATS_PER_VERTEX * 4, offset * 4);
    }

    private static void attribute(
        int program, String name, FloatBuffer data, int offset, int size, int stride) {
      int location = GLES20.glGetAttribLocation(program, name);
      data.position(offset);
      GLES20.glEnableVertexAttribArray(location);
      GLES20.glVertexAttribPointer(location, size, GLES20.GL_FLOAT, false, stride * 4, data);
    }

    private static FloatBuffer buffer(float[] values) {
      FloatBuffer data =
          ByteBuffer.allocateDirect(values.length * 4)
              .order(ByteOrder.nativeOrder())
              .asFloatBuffer();
      data.put(values).position(0);
      return data;
    }

    private static int shader(int type, String source) {
      int shader = GLES20.glCreateShader(type);
      GLES20.glShaderSource(shader, source);
      GLES20.glCompileShader(shader);
      int[] status = new int[1];
      GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
      if (status[0] == 0) throw new IllegalStateException(GLES20.glGetShaderInfoLog(shader));
      return shader;
    }

    private static int program(String vertex, String fragment) {
      int vertexShader = shader(GLES20.GL_VERTEX_SHADER, vertex);
      int fragmentShader = shader(GLES20.GL_FRAGMENT_SHADER, fragment);
      int program = GLES20.glCreateProgram();
      GLES20.glAttachShader(program, vertexShader);
      GLES20.glAttachShader(program, fragmentShader);
      GLES20.glLinkProgram(program);
      GLES20.glDeleteShader(vertexShader);
      GLES20.glDeleteShader(fragmentShader);
      int[] status = new int[1];
      GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
      if (status[0] == 0) throw new IllegalStateException(GLES20.glGetProgramInfoLog(program));
      return program;
    }

    private static final String PIPE_VERTEX =
        "uniform mat4 uMatrix; attribute vec3 aPosition,aNormal,aColor; attribute float aGrowth,aTail;"
            + "varying vec3 vPosition,vNormal,vColor; varying float vGrowth,vTail;"
            + "void main(){vPosition=aPosition;vNormal=aNormal;vColor=aColor;vGrowth=aGrowth;vTail=aTail;gl_Position=uMatrix*vec4(aPosition,1.0);}";
    private static final String PIPE_FRAGMENT =
        "precision mediump float; varying vec3 vPosition,vNormal,vColor; varying float vGrowth,vTail;"
            + "uniform vec3 uEye; uniform float uLight,uGlow,uGrowth;"
            + "void main(){if(vGrowth>uGrowth||vTail<uGrowth)discard;vec3 n=normalize(vNormal);vec3 v=normalize(uEye-vPosition);"
            + "vec3 l=normalize(vec3(-.6,.85,1.));vec3 h=normalize(l+v);"
            + "float diffuse=max(0.,dot(n,l));float spec=pow(max(0.,dot(n,h)),75.);"
            + "float rim=pow(1.-max(0.,dot(n,v)),3.);vec3 r=reflect(-v,n);"
            + "float strip=pow(max(0.,1.-abs(r.y-.45)*2.2),16.);"
            + "float fill=max(0.,dot(n,normalize(vec3(.8,.1,-.6))));"
            + "vec3 c=vColor*(.13+diffuse*.66+fill*.15)*uLight;"
            + "c+=mix(vColor,vec3(1.),.65)*(spec*1.15+strip*.32)*uLight;"
            + "c+=vec3(.14,.23,.3)*rim*.4+vColor*rim*.1*uGlow;"
            + "c=mix(c,vec3(.015,.022,.03),clamp((length(uEye-vPosition)-19.)*.012,0.,.22));"
            + "gl_FragColor=vec4(pow(c,vec3(.8)),1.);}";
    private static final String BACKGROUND_VERTEX =
        "attribute vec2 aPosition;void main(){gl_Position=vec4(aPosition,0.,1.);}";
    private static final String BACKGROUND_FRAGMENT =
        "precision mediump float;uniform vec2 uResolution;uniform float uTime,uStyle;"
            + "void main(){vec2 uv=gl_FragCoord.xy/uResolution;vec2 p=uv-vec2(.5+.08*sin(uTime*.025),.55);"
            + "vec3 base=uStyle<.5?vec3(.009,.014,.025):uStyle<1.5?vec3(.009,.025,.035):vec3(.028,.032,.04);"
            + "vec3 light=uStyle<.5?vec3(.05,.034,.025):uStyle<1.5?vec3(.015,.065,.075):vec3(.05,.055,.062);"
            + "float noise=fract(sin(dot(gl_FragCoord.xy,vec2(12.9898,78.233)))*43758.5453)/255.;"
            + "gl_FragColor=vec4(base+light*exp(-dot(p,p)*4.)+noise,1.);}";
  }
}
