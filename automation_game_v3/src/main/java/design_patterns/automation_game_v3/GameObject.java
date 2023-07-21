package design_patterns.automation_game_v3;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
public class GameObject {
    private final GraphicsContext gc;
    private Image img;
    private double x;
    private  double y;
    /**
     * Constructor for GameObjects
     * @param gc Graphics context GameObject will be drawn  on
     * @param x x position on graphics context
     * @param y y position on graphics context
     */
    public GameObject(GraphicsContext gc, double x, double y)
    {
        this.gc=gc;
        this.x=x;
        this.y=y;
    }
    /**
     * Draw image
     */
    public void update()
    {
        if(img!=null)
            gc.drawImage(img, x, y, 30, 30);
    }
    public void setImg(Image img) {        this.img = img;    }
    public void setX(double x) {        this.x = x;    }
    public void increaseX(double x){        this.x+=x;    }
    public void  increaseY(double y){        this.y+=y;    }

    public void setY(double y) {        this.y = y;   }
    public Image getImg() {        return img;    }
    public double getX() {        return x;    }
    public double getY() {        return y;    }
    public GraphicsContext getGc() {        return gc;    }

}