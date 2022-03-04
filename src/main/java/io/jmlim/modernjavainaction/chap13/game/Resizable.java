package io.jmlim.modernjavainaction.chap13.game;

public interface Resizable extends Drawable {

    int getWidth();

    int getHeight();

    void setWidth(int width);

    void setHeight(int height);

    void setAbsoluteSize(int width, int height);
    //TODO: uncomment, read the README for instructions
    //void setRelativeSize(int widthFactor, int heightFactor);

    /**
     * 디폴트 메서드를 이용해서 setRelativeSize 의 디폴트 구현을 제공한다면 호환성을 유지하면서 라이브러리를 고칠 수 있다.
     * @param widthFactor
     * @param heightFactor
     */
    default void setRelativeSize(int widthFactor, int heightFactor) {
       setAbsoluteSize(getWidth() / widthFactor, getHeight() / heightFactor);
    }
}
