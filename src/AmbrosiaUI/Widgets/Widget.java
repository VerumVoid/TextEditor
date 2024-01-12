package AmbrosiaUI.Widgets;

import AmbrosiaUI.Utility.EventStatus;
import AmbrosiaUI.Utility.Position;
import AmbrosiaUI.Utility.Rectangle;
import AmbrosiaUI.Utility.Size;
import AmbrosiaUI.Widgets.Placements.Placement;
import AmbrosiaUI.Widgets.Placements.PlacementCell;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

public abstract class Widget implements Comparable<Widget>{
    protected Placement placement;
    protected Placement childrenPlacement;

    protected Theme theme;
    protected int placementIndex;

    protected boolean mouseOver = false;

    protected Position lastMousePosition = new Position(0,0);

    protected int zIndex = 0;

    protected int margin = 0;

    protected boolean disabled = false;

    protected final boolean DEBUG = false;

    public void draw(Graphics2D g2) {
        ArrayList<Widget> toBeDrawn = this.getAllChildren();

        Collections.sort(toBeDrawn);

        for(Widget w: toBeDrawn){
            w.drawSelf(g2);
        }
    }
    public void drawSelf(Graphics2D g2){
        if(!DEBUG){
            return;
        }

        if(this.childrenPlacement != null){
            this.childrenPlacement.drawDebug(g2);
        }

        g2.setFont(new Font("Monospaced", Font.PLAIN,10));
        FontMetrics fm = g2.getFontMetrics(g2.getFont());

        g2.setColor(new Color(255,0,0));

        String text = " " + this.getX() + ":" + this.getY();
        int text_x = this.getX();
        int text_y = this.getY();


        g2.drawRect(this.getX(),this.getY(),this.getWidth()-1,this.getHeight()-1);

        g2.drawLine(this.getX(),this.getY(),this.getX()+this.getWidth(),this.getY()+this.getHeight());
        g2.drawLine(this.getX()+this.getWidth(),this.getY(),this.getX(),this.getY()+this.getHeight());

        g2.setColor(new Color(0,0,0));
        g2.fillRect(text_x,text_y,fm.stringWidth(text)+10, fm.getAscent()+2);

        g2.setColor(new Color(255,0,0));
        g2.drawRect(text_x,text_y,fm.stringWidth(text)+10, fm.getAscent()+2);
        g2.drawString(text, text_x,text_y+fm.getAscent());
    }
    public void setupDraw(Graphics2D g2){
        g2.setStroke(new BasicStroke(1));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(theme.getFontByName("normal"));
        g2.setClip(this.getX(),this.getY(),this.getWidth(),this.getHeight());
    }
    public Position getPosition(){
        return placement.getPosition(placementIndex).getOffset(margin, margin);
    }
    public int getWidth(){
        return placement.getWidth(placementIndex) - margin*2;
    }
    public int getHeight(){
        return placement.getHeight(placementIndex) - margin*2;
    }

    public int getX(){
        return this.getPosition().x;
    }
    public int getY(){
        return this.getPosition().y;
    }

    public Placement getPlacement() {
        return placement;
    }

    public void setPlacement(Placement placement) {
        this.placement = placement;
    }

    public int getPlacementIndex() {
        return placementIndex;
    }

    public void setPlacementIndex(int placementIndex) {
        this.placementIndex = placementIndex;
    }

    public Placement getChildrenPlacement() {
        return childrenPlacement;
    }

    public void setChildrenPlacement(Placement childrenPlacement) {
        this.childrenPlacement = childrenPlacement;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme Theme) {
        this.theme = Theme;
    }

    public ArrayList<Widget> getChildren(){
        ArrayList<Widget> output = new ArrayList<>();

        if(this.childrenPlacement != null){
            for (PlacementCell cell: this.childrenPlacement.getChildren() ){
                output.add(cell.getBoundElement());
            }
        }

        return output;
    }

    public ArrayList<Widget> getAllChildren(){
        ArrayList<Widget> output = new ArrayList<>();

        if(this.childrenPlacement != null){
            for (Widget w: this.getChildren()){
                output.add(w);
                output.addAll(w.getAllChildren());
            }
        }

        return output;
    }

    public void fullUpdate(EventStatus eventStatus){
        this.mouseOver = false;
        this.getChildUnderMouse().mouseOver = true;
        this.update(eventStatus);
    }

    public void update(EventStatus eventStatus){
        boolean found = false;

        for(Rectangle rect: this.getMouseHoverRectangles()){
            if(eventStatus.getMousePosition().inRectangle(rect)){
                found = true;
                break;
            }
        }

        mouseOver = found;
        lastMousePosition = eventStatus.getMousePosition();

        for(Widget w: this.getChildren()){
            w.update(eventStatus);

            if(w.mouseOver){
                this.mouseOver = false;
            }
        }

        if(disabled){
            mouseOver = false;
        }
    }

    public Widget getChildUnderMouse(){
        Widget output = this;

        ArrayList<Widget> children = getAllChildren();
        ArrayList<Widget> childrenUnderCursor = new ArrayList<>();

        for(Widget child: children){
            if(child.mouseOver){
                childrenUnderCursor.add(child);
            }
        }

        Collections.sort(childrenUnderCursor);

        if(!childrenUnderCursor.isEmpty()){
            output = childrenUnderCursor.get(childrenUnderCursor.size()-1);
        }

        return output;
    }

    public Rectangle getBoundingRect(){
        return new Rectangle(this.getPosition(),this.getWidth(),this.getHeight());
    }

    public ArrayList<Rectangle> getMouseHoverRectangles(){
        ArrayList<Rectangle> rects = new ArrayList<>();
        rects.add(this.getBoundingRect());
        return rects;
    }
    public int getzIndex() {
        return zIndex;
    }

    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public Size getSize(){
        return new Size(this.getWidth(),this.getHeight());
    }

    @Override
    public int compareTo(Widget o) {
        return Integer.compare(this.zIndex, o.getzIndex());
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "Widget{" +
                "placement=" + placement +
                ", childrenPlacement=" + childrenPlacement +
                ", theme=" + theme +
                ", placementIndex=" + placementIndex +
                ", mouseOver=" + mouseOver +
                ", zIndex=" + zIndex +
                '}';
    }

    public void onMouseDragged(MouseEvent e){

    }

    public void onMouseMoved(MouseEvent e){

    }

    public void onMouseClicked(MouseEvent e){

    }

    public void onMousePressed(MouseEvent e){

    }

    public void onMouseReleased(MouseEvent e){

    }

    public void onKeyPressed(KeyEvent keyEvent){

    }

    public void onKeyReleased(KeyEvent keyEvent){

    }
}