/** Una vista falsa: cumple el contrato sin abrir ninguna ventana. */
class FakeView implements AnimationContract.View {

    int fps = 60;
    boolean started;
    boolean stopped;
    AnimationContract.Presenter presenter;

    @Override public void setPresenter(AnimationContract.Presenter presenter) { this.presenter = presenter; }
    @Override public void start() { started = true; }
    @Override public void stop() { stopped = true; }
    @Override public int renderFps() { return fps; }
    @Override public void setRenderFps(int fps) { this.fps = fps; }
}
