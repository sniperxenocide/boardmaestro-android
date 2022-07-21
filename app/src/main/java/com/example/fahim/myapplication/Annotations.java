package com.example.fahim.myapplication;

import android.graphics.PointF;

import java.util.ArrayList;

public class Annotations
{
    public ArrayList<MyFreehandDraw > FreehandDrawing;    // 0
    public ArrayList<MyRectangle> Rectangles;             // 1
    public ArrayList<MyCircle> Circles;                   // 2
    public ArrayList<MyLine> Lines;                       // 3
    public ArrayList<MyText> Texts;                       // 4
    public ArrayList<MyMarker> Markers;                   // 5

    public Annotations()
    {
        FreehandDrawing = new ArrayList<>();
        Rectangles = new ArrayList<>();
        Circles = new ArrayList<>();
        Lines = new ArrayList<>();
        Texts = new ArrayList<>();
        Markers = new ArrayList<>();
    }
    
    
    public void clearAll()
    {
        this.FreehandDrawing.clear();
        this.Rectangles.clear();
        this.Circles.clear();
        this.Lines.clear();
        this.Texts.clear();
        this.Markers.clear();
    }

}

class MyRectangle
{
    public PointF leftTop;
    public PointF rightBottom;
    public MyRectangle(PointF a, PointF b)
    {
        leftTop = a;
        rightBottom = b;
    }
}

class MyCircle
{
    public PointF left;
    public PointF right;
    public MyCircle(PointF a, PointF b)
    {
        left = a;
        right = b;
    }
}

class MyLine
{
    public PointF a;
    public PointF b;
    MyLine(PointF x, PointF y)
    {
        a=x;
        b=y;
    }
}

class MyText
{
    public PointF a;
    String text;
    MyText(PointF x, String t)
    {
        a=x;
        text = t;
    }
}


class MyFreehandDraw
{
    ArrayList<PointF> drawing;
    public MyFreehandDraw()
    {
        drawing = new ArrayList<>();
    }
}

class MyMarker
{
    ArrayList<PointF> markers;
    public MyMarker()
    {
        markers = new ArrayList<>();
    }
}

