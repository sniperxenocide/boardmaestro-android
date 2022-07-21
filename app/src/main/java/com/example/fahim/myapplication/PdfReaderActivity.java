package com.example.fahim.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Stack;



public class PdfReaderActivity extends AppCompatActivity implements View.OnTouchListener{




    private final static int REQUEST_CODE = 42;
    public static File file ;
    public ArrayList<Annotations> AnnotationList ;
    public ArrayList<Stack<Integer>> UndoStackList ;
    public MyFreehandDraw myDrawing ;
    public MyMarker myMarker;
    public ThumbnailAdapter adapter;
    public ListView thumbnailListView;
    public ListView pdfPageListView;
    public PdfPageAdapter pageAdapter;

    public static int MODE ;
    public static int READ_MODE ;
    public static int GRAB_MODE;
    public static int DRAW_RANDOM ;
    public static int DRAW_LINE ;
    public static int DRAW_CIRCLE;
    public static int DRAW_RECTANGLE;
    public static int MARKER_MODE;
    public static int TEXT_MODE;

    public static int currentPage = 0;
    public static int lastSelectedPage =0;
    public static int TOTAL_PAGE ;
    public float downx = 0, downy = 0, upx = 0, upy = 0, offsetx = 0, offsety = 0, offsetScrollX=0, offsetScrollY=0, mx=0, my=0, mScaleFactor = 1.0f , zoomFactor = 1.0f;
    int diffX = 0,diffY = 0;
    int PAGE_WIDTH;
    int PAGE_HEIGHT;



    public ScaleGestureDetector mScaleGestureDetector;
    ImageView imageView;
    Canvas canvas;
    Paint paint = new Paint();
    Bitmap bitmap;



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String PathHolder = data.getData().getPath();
                    System.out.println(PathHolder+ "***********************************************************");
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(PdfReaderActivity.this, "Permission denied to access your location.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void zoomIn(View view) {
        try{
            if(MODE == READ_MODE)
            {
                if(zoomFactor <= 9.5) zoomFactor += 0.5;
                pdfPageListView.setScaleX(zoomFactor);
                pdfPageListView.setScaleY(zoomFactor);
            }
            else {
                if(mScaleFactor <= 9.5) mScaleFactor += 0.5;
                imageView.setScaleX(mScaleFactor);
                imageView.setScaleY(mScaleFactor);
            }
        }catch (Exception e){e.printStackTrace();}
    }
    public void zoomOut(View view) {
        try{
            if(MODE == READ_MODE){
                if(zoomFactor >= 1.5) zoomFactor -= 0.5;
                pdfPageListView.setScaleX(zoomFactor);
                pdfPageListView.setScaleY(zoomFactor);
            }
            else {
                if(mScaleFactor>=1.5) mScaleFactor -= 0.5;
                imageView.setScaleX(mScaleFactor);
                imageView.setScaleY(mScaleFactor);
            }
        }catch (Exception e){e.printStackTrace();}
    }
    public void clearCanvas(View view) {
        if(MODE != READ_MODE)
        {
            AnnotationList.get(currentPage).clearAll();
            UndoStackList.get(currentPage).clear();

            SaveAnnotation();     // saving annotation in Database
            showPage();
        }

    }
    public void undoAction(View view) {
        try{
            if(!UndoStackList.get(currentPage).isEmpty() && MODE != READ_MODE)
            {
                int x = UndoStackList.get(currentPage).pop();
                if(x == 0)
                {
                    AnnotationList.get(currentPage).FreehandDrawing.remove( AnnotationList.get(currentPage).FreehandDrawing.size()-1 );
                }
                else if(x ==1 )
                {
                    AnnotationList.get(currentPage).Rectangles.remove( AnnotationList.get(currentPage).Rectangles.size()-1 );
                }
                else if(x == 2)
                {
                    AnnotationList.get(currentPage).Circles.remove( AnnotationList.get(currentPage).Circles.size()-1 );
                }
                else if(x == 3)
                {
                    AnnotationList.get(currentPage).Lines.remove( AnnotationList.get(currentPage).Lines.size()-1 );
                }
                else if(x == 4)
                {
                    AnnotationList.get(currentPage).Texts.remove( AnnotationList.get(currentPage).Texts.size()-1 );
                }
                if(x == 5)
                {
                    AnnotationList.get(currentPage).Markers.remove( AnnotationList.get(currentPage).Markers.size()-1 );
                }

                SaveAnnotation();     // saving annotation in Database
                showPage();
            }
        }catch (Exception e){e.printStackTrace();}


    }
    public void toggleFunction(View view) {
        normalizeButtonColor();
        view.setBackgroundColor(Color.RED);
        MODE = view.getId();

        if(MODE == READ_MODE)
        {
            initListViewAdapter();
            pdfPageListView.setVisibility(View.VISIBLE);
            pdfPageListView.setSelection(lastSelectedPage);
            thumbnailListView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }
        else
        {
            lastSelectedPage = currentPage;
            imageView.setVisibility(View.VISIBLE);
            showPage();
            thumbnailListView.setVisibility(View.INVISIBLE);
            pdfPageListView.setVisibility(View.GONE);
        }

    }
    public void goToPage(View view) {
        EditText e = findViewById(R.id.getPage);
        String s = e.getText().toString();
        try
        {
            int p = Integer.parseInt(s)-1;
            if(p>=0 && p< TOTAL_PAGE)
            {
                currentPage = p;
                lastSelectedPage = currentPage;
                pdfPageListView.setSelection(currentPage);
                adjustThumbnailMarker(currentPage);

                showPage();
            }
            else showToast("Page doesnt exist");
            hideKeyboard(view);
        }catch (Exception x)
        {
            showToast("Page doesnt exist");
            return;
        }
    }
    public void closeActivity(View view) {
        this.finish();
    }

    public void setPageResolution() {
        Display currentDisplay = getWindowManager().getDefaultDisplay();
        float dw = currentDisplay.getWidth();
        float dh = currentDisplay.getHeight();

        PAGE_WIDTH = (int)(dw*0.6);
        PAGE_HEIGHT = (int)(dh*0.93);
    }

    public void normalizeButtonColor() {
        ConstraintLayout b  = findViewById(GRAB_MODE);
        b.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.lightGrey));

        b  = findViewById(READ_MODE);
        b.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.lightGrey));

        b= findViewById(DRAW_RANDOM);
        b.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.lightGrey));

        b= findViewById(MARKER_MODE);
        b.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.lightGrey));

        b= findViewById(DRAW_LINE);
        b.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.lightGrey));

        b= findViewById(DRAW_CIRCLE);
        b.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.lightGrey));

        b= findViewById(DRAW_RECTANGLE);
        b.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.lightGrey));

        b= findViewById(TEXT_MODE);
        b.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.lightGrey));
    }

    public void adjustThumbnailMarker(int pageNo) {
        thumbnailListView.setSelection(pageNo);
        for (int i=0;i<adapter.checked.length;i++) {
            adapter.checked[i] = false;
            if(adapter.layouts[i]!= null) adapter.layouts[i].setBackgroundColor(Color.TRANSPARENT);
        }
        if(adapter.layouts[pageNo]!= null) adapter.layouts[pageNo].setBackgroundColor(getResources().getColor(R.color.lightBlue));
        adapter.checked[pageNo] = true;

    }

    public void initModes() {
        READ_MODE = R.id.read;
        GRAB_MODE = R.id.grab;
        DRAW_RANDOM = R.id.draw;
        DRAW_LINE = R.id.line;
        DRAW_CIRCLE = R.id.circle;
        DRAW_RECTANGLE = R.id.rectangle;
        MARKER_MODE = R.id.mark;
        TEXT_MODE = R.id.text;
        MODE = READ_MODE;
        normalizeButtonColor();
        findViewById(READ_MODE).setBackgroundColor(Color.RED);
    }

    public void initPaint() {
        paint.setStyle(Paint.Style.STROKE);      // doesn't fill the geometric figure with color
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
        //paint.setAlpha(200);
    }

    public void initListViewAdapter() {
        pdfPageListView = findViewById(R.id.listPdf);
        ArrayList<Integer> list = new ArrayList<>();
        try{
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
            PdfRenderer renderer = new PdfRenderer(fileDescriptor);
            TOTAL_PAGE = renderer.getPageCount();
            for(int i=0;i<TOTAL_PAGE;i++)
            {
                list.add(i);
            }
            renderer.close();
        }catch (Exception e){e.printStackTrace();}
        try{
            pageAdapter = new PdfPageAdapter(list,getApplicationContext(),PdfReaderActivity.this,file);
            pdfPageListView.setAdapter(pageAdapter);
        }catch (Exception e){e.printStackTrace();}
    }


    public void setListViewScrollListener() {
        pdfPageListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                    TextView pageStatus = findViewById(R.id.pageStatus);
                    EditText getPage = findViewById(R.id.getPage);
                    getPage.setText(Integer.toString(currentPage+1));
                    pageStatus.setText("of "+TOTAL_PAGE+" ");
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                currentPage = firstVisibleItem;
                adjustThumbnailMarker(firstVisibleItem);
                hideKeyboard(view);
            }
        });
    }

    public void loadNewFile(String filename) {
        file = new File(PdfReaderActivity.this.getFilesDir(),filename);
        try {
            prepareThumbnail(file);
        }catch (Exception e){e.printStackTrace();}

        InitAnnotationList();
        initModes();
        initPaint();
        initListViewAdapter();

        pdfPageListView.setVisibility(View.VISIBLE);
        thumbnailListView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);

        setListViewScrollListener();
        showToast("New File Opened");
    }

    public void prepareThumbnail(File file) {
        try{
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
            PdfRenderer renderer = new PdfRenderer(fileDescriptor);
            TOTAL_PAGE = renderer.getPageCount();

            TextView pageStatus = findViewById(R.id.pageStatus);
            pageStatus.setText("of "+TOTAL_PAGE+" ");

            ArrayList<Bitmap> list = new ArrayList<>();
            thumbnailListView = findViewById(R.id.thumbnail);
            for(int i=0;i<TOTAL_PAGE;i++)
            {
                PdfRenderer.Page page = renderer.openPage(i);
                Display currentDisplay = getWindowManager().getDefaultDisplay();
                float dw = currentDisplay.getWidth();
                float dh = currentDisplay.getHeight();

                int width = (int)(dw*0.05);
                int height = (int)(dh*0.08);

                Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                page.render(bm, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                list.add(bm);
                page.close();
            }

            try{
                adapter = new ThumbnailAdapter(list, getApplicationContext(),PdfReaderActivity.this, 0);
                thumbnailListView.setAdapter(adapter);
            }catch (Exception e){e.printStackTrace();}


        }catch (Exception e){e.printStackTrace();}

    }

    public void initAgendaList(String filename) {
        ArrayList<JSONObject> list = new ArrayList<>();
        ListView listView = findViewById(R.id.agendaListPdf);
        try{
            JSONArray agendaList  = new JSONArray(getIntent().getExtras().getString("agenda"));
            System.out.println(agendaList.toString());
            if(agendaList!=null){
                findViewById(R.id.agendaContainer).setVisibility(View.VISIBLE);
                for(int i=0;i<agendaList.length();i++){
                    JSONArray files = agendaList.getJSONObject(i).getJSONArray("files");
                    {
                        for(int j=0;j<files.length();j++)
                        {
                            try{
                                JSONObject object = new JSONObject();
                                object.put("id",agendaList.getJSONObject(i).getString("id"));
                                object.put("file_id",files.getJSONObject(j).getString("id"));
                                object.put("file_name_actual",files.getJSONObject(j).getString("title"));
                                object.put("serial_no",agendaList.getJSONObject(i).getString("serial_no"));
                                object.put("title",agendaList.getJSONObject(i).getString("title"));
                                object.put("filename",files.getJSONObject(j).getString("location").replace("/","_"));
                                list.add(object);
                            }catch (Exception e){e.printStackTrace();}
                        }
                    }
                }
            }

        }  catch (Exception e){e.printStackTrace();}
        try{
            PdfAgendaAdapter adapter = new PdfAgendaAdapter(list,getApplicationContext(),PdfReaderActivity.this, filename);
            listView.setAdapter(adapter);
        }catch (Exception e){e.printStackTrace();}

    }

    public void showHideAgendaList(View view) {
        TextView textView = findViewById(R.id.showHideAgendaList);
        String text = textView.getText().toString();
        String show = "Show Agenda List";
        String hide = "Hide Agenda List";
        if(text.equals(show))
        {
            textView.setText(hide);
            findViewById(R.id.agendaListPdf).setVisibility(View.VISIBLE);
        }
        else if(text.equals(hide)){
            textView.setText(show);
            findViewById(R.id.agendaListPdf).setVisibility(View.INVISIBLE);
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>22){ requestPermissions(new String[] {"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE"}, 1); }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pdf_reader);
        imageView = findViewById(R.id.singlePageImageview);
        setPageResolution();

        try{
            String filename = getIntent().getExtras().getString("filename");
            initAgendaList(filename);
            loadNewFile(filename);
        }catch (Exception e){e.printStackTrace();}

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    public void InitAnnotationList() {
        try{
            AnnotationList = new ArrayList<>();
            UndoStackList = new ArrayList<>();

            for(int i=0;i<TOTAL_PAGE;i++) {
                AnnotationList.add(new Annotations());
                UndoStackList.add(new Stack<Integer>());
            }

            try{
                JSONObject object = new JSONObject(MainActivity.dbHelper.getData(file.getName()));
                String annotation = object.getString("annotation");
                String stack = object.getString("stack");

                Gson gson = new Gson();

                Type type1 = new TypeToken<ArrayList<Annotations>>() {}.getType();
                Type type2 = new TypeToken<ArrayList<Stack<Integer>>>() {}.getType();

                AnnotationList = gson.fromJson(annotation, type1);
                UndoStackList = gson.fromJson(stack,type2);
            }catch (Exception e){e.printStackTrace();}


        }catch (Exception e){e.printStackTrace();}

    }

    public void LoadAnnotation(int pageNo, float page_width, float page_height) {
        try{
            for (MyLine l: AnnotationList.get(pageNo).Lines)
            {
                canvas.drawLine(l.a.x*page_width, l.a.y*page_height, l.b.x*page_width , l.b.y*page_height, paint);
            }

            for (MyCircle c: AnnotationList.get(pageNo).Circles)
            {
                canvas.drawCircle( (c.left.x*page_width + c.right.x*page_width)/2 , (c.left.y*page_height + c.right.y*page_height)/2 ,
                        ((float) Math.sqrt( (c.left.x*page_width - c.right.x*page_width)*(c.left.x*page_width - c.right.x*page_width)
                                + (c.left.y*page_height - c.right.y*page_height)*(c.left.y*page_height - c.right.y*page_height)  ))/2 , paint );
            }

            for (MyRectangle r: AnnotationList.get(pageNo).Rectangles)
            {
                canvas.drawLine(r.leftTop.x*page_width,  r.leftTop.y*page_height,  r.leftTop.x*page_width,   r.rightBottom.y*page_height,   paint);
                canvas.drawLine(r.leftTop.x*page_width,  r.leftTop.y*page_height,  r.rightBottom.x*page_width,   r.leftTop.y*page_height,   paint);
                canvas.drawLine(r.rightBottom.x*page_width,  r.leftTop.y*page_height,  r.rightBottom.x*page_width,   r.rightBottom.y*page_height,   paint);
                canvas.drawLine(r.leftTop.x*page_width,  r.rightBottom.y*page_height,  r.rightBottom.x*page_width,   r.rightBottom.y*page_height,   paint);
            }



            for (MyFreehandDraw f: AnnotationList.get(pageNo).FreehandDrawing)
            {
                for (int i=0;i< f.drawing.size()-1;i++)
                {
                    PointF a = f.drawing.get(i);
                    PointF b = f.drawing.get(i+1);
                    canvas.drawLine(a.x*page_width,a.y*page_height,b.x*page_width,b.y*page_height,paint);
                }
            }


            paint.setColor(Color.YELLOW);
            paint.setAlpha(100);
            paint.setStrokeWidth(30);
            for (MyMarker f: AnnotationList.get(pageNo).Markers)
            {
                for (int i=0;i< f.markers.size()-1;i++)
                {
                    PointF a = f.markers.get(i);
                    PointF b = f.markers.get(i+1);
                    canvas.drawLine(a.x*page_width,a.y*page_height,b.x*page_width,b.y*page_height,paint);
                }
            }
            initPaint();


            for (MyText f: AnnotationList.get(pageNo).Texts)
            {
                canvas.drawText(f.text, f.a.x*page_width ,f.a.y*page_height ,paint);
            }
        }catch (Exception e){e.printStackTrace();}


    }

    public void SaveAnnotation() {
        try{
            Gson gson = new Gson();
            String annotation = gson.toJson(AnnotationList);
            String stack = gson.toJson(UndoStackList);
            JSONObject object = new JSONObject();
            object.put("annotation",annotation);
            object.put("stack",stack);
            MainActivity.dbHelper.insertData(file.getName(),object.toString());
        }catch (Exception e){e.printStackTrace();}
    }

    public void showPage() {
        try
        {
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
            PdfRenderer renderer = new PdfRenderer(fileDescriptor);
            TOTAL_PAGE = renderer.getPageCount();


            PdfRenderer.Page page = renderer.openPage(currentPage);

            bitmap = Bitmap.createBitmap(PAGE_WIDTH, PAGE_HEIGHT, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            canvas = new Canvas(bitmap);
            imageView.setImageBitmap(bitmap);
            imageView.setOnTouchListener(this);
            TextView pageStatus = findViewById(R.id.pageStatus);
            EditText getPage = findViewById(R.id.getPage);
            getPage.setText(Integer.toString(currentPage+1));
            pageStatus.setText("of "+TOTAL_PAGE+" ");

            LoadAnnotation(currentPage, PAGE_WIDTH, PAGE_HEIGHT);

            page.close();
            renderer.close();
            imageView.invalidate();

        }catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if(MODE == READ_MODE) readModeAction(view,event);
        else if(MODE == GRAB_MODE) grabModeAction(view,event);
        else if(MODE == DRAW_RANDOM) drawRandomAction(view,event);
        else if(MODE == DRAW_LINE) drawLineAction(view,event);
        else if(MODE == DRAW_CIRCLE) drawCircleAction(view,event);
        else if(MODE == DRAW_RECTANGLE) drawRectangleAction(view,event);
        else if(MODE == TEXT_MODE) textModeAction(view,event);
        else if(MODE == MARKER_MODE) markerModeAction(view,event);
        return true;
    }
    public void readModeAction(View view, MotionEvent event) {
        //mScaleGestureDetector.onTouchEvent(event);
        float curX=0, curY=0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mx = event.getX();
                my = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                if( Math.abs(offsetScrollX)<=pageAdapter.Width/4 && Math.abs(offsetScrollY)<=pageAdapter.Height/4 )
                {
                    curX = event.getX();
                    curY = event.getY();

                    diffX = (int)(mx - curX);
                    diffY = (int)(my - curY);

                    offsetScrollX +=  diffX;
                    offsetScrollY +=  diffY;

                    for(int i=0;i<pageAdapter.pageList.size();i++)
                        if(pageAdapter.pageList.get(i) !=null) pageAdapter.pageList.get(i).scrollBy(diffX, 0);

                    mx = curX;
                    my = curY;
                }
                else {
                    offsetScrollX -= diffX;
                    offsetScrollY -= diffY;

                    for(int i=0;i<pageAdapter.pageList.size();i++)
                        if(pageAdapter.pageList.get(i) !=null) pageAdapter.pageList.get(i).scrollBy(-diffX, 0);

                }

                break;
            case MotionEvent.ACTION_UP:
                if( Math.abs(offsetScrollX)<=pageAdapter.Width/4 && Math.abs(offsetScrollY)<=pageAdapter.Height/4 )
                {
                    curX = event.getX();
                    curY = event.getY();

                    diffX = (int)(mx - curX);
                    diffY = (int)(my - curY);

                    offsetScrollX +=  diffX;
                    offsetScrollY +=  diffY;

                    for(int i=0;i<pageAdapter.pageList.size();i++)
                        if(pageAdapter.pageList.get(i) !=null) pageAdapter.pageList.get(i).scrollBy(diffX, 0);

                }
                break;
        }
    }
    public void grabModeAction(View view, MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        float curX=0, curY=0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mx = event.getX();
                my = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                if( Math.abs(offsetx)<=imageView.getWidth()/4 && Math.abs(offsety)<=imageView.getHeight()/4 )
                {
                    curX = event.getX();
                    curY = event.getY();

                    diffX = (int)(mx - curX);
                    diffY = (int)(my - curY);

                    offsetx +=  diffX;
                    offsety +=  diffY;

                    imageView.scrollBy(diffX, diffY);

                    mx = curX;
                    my = curY;
                }
                else {
                    offsetx -= diffX;
                    offsety -= diffY;
                    imageView.scrollBy(-diffX, -diffY);


                }

                break;
            case MotionEvent.ACTION_UP:
                if( Math.abs(offsetx)<=imageView.getWidth()/4 && Math.abs(offsety)<=imageView.getHeight()/4 )
                {
                    curX = event.getX();
                    curY = event.getY();

                    diffX = (int)(mx - curX);
                    diffY = (int)(my - curY);

                    offsetx +=  diffX;
                    offsety +=  diffY;

                    imageView.scrollBy(diffX, diffY);

                }


                break;
        }
    }
    public void drawRandomAction(View view, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX()+offsetx;
                downy = event.getY()+offsety;

                myDrawing = new MyFreehandDraw();
                myDrawing.drawing.add(new PointF(downx/(float)PAGE_WIDTH, downy/(float)PAGE_HEIGHT));                 // saving annotation

                break;
            case MotionEvent.ACTION_MOVE:
                upx = event.getX()+offsetx;
                upy = event.getY()+offsety;

                myDrawing.drawing.add(new PointF(upx/(float)PAGE_WIDTH ,upy/(float)PAGE_HEIGHT));                 // saving annotation

                canvas.drawLine(downx, downy, upx, upy, paint);
                imageView.invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX()+offsetx;
                upy = event.getY()+offsety;

                myDrawing.drawing.add(new PointF(upx/(float)PAGE_WIDTH, upy/(float)PAGE_HEIGHT));                 // saving annotation
                AnnotationList.get(currentPage).FreehandDrawing.add(myDrawing);                 // saving annotation
                UndoStackList.get(currentPage).push(0);

                SaveAnnotation();     // saving annotation in Database

                canvas.drawLine(downx, downy, upx, upy, paint);
                imageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
    }
    public void drawLineAction(View view, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX()+offsetx;
                downy = event.getY()+offsety;
                break;
            case MotionEvent.ACTION_MOVE:
                showPage();
                upx = event.getX()+offsetx;
                upy = event.getY()+offsety;
                canvas.drawLine(downx, downy, upx, upy, paint);
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX()+offsetx;
                upy = event.getY()+offsety;

                // saving annotation
                AnnotationList.get(currentPage).Lines.add(new MyLine( new PointF(downx/(float)PAGE_WIDTH, downy/(float)PAGE_HEIGHT) , new PointF(upx/(float)PAGE_WIDTH, upy/(float)PAGE_HEIGHT)));
                UndoStackList.get(currentPage).push(3);

                SaveAnnotation();        // saving annotation in Database

                canvas.drawLine(downx, downy, upx, upy, paint);
                imageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
    }
    public void drawCircleAction(View view, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX()+offsetx;
                downy = event.getY()+offsety;
                break;
            case MotionEvent.ACTION_MOVE:
                showPage();
                upx = event.getX()+offsetx;
                upy = event.getY()+offsety;
                canvas.drawCircle( (downx+upx)/2 , (downy+upy)/2 , ((float) Math.sqrt( (downx-upx)*(downx-upx) + (downy-upy)*(downy-upy)  ))/2 , paint );
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX()+offsetx;
                upy = event.getY()+offsety;

                // saving annotation
                AnnotationList.get(currentPage).Circles.add(new MyCircle( new PointF(downx/(float)PAGE_WIDTH, downy/(float)PAGE_HEIGHT) , new PointF(upx/(float)PAGE_WIDTH, upy/(float)PAGE_HEIGHT)));
                UndoStackList.get(currentPage).push(2);

                SaveAnnotation();     // saving annotation in Database

                canvas.drawCircle( (downx+upx)/2 , (downy+upy)/2 , ((float) Math.sqrt( (downx-upx)*(downx-upx) + (downy-upy)*(downy-upy)  ))/2 , paint );
                imageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
    }
    public void drawRectangleAction(View view, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX()+offsetx;
                downy = event.getY()+offsety;
                break;
            case MotionEvent.ACTION_MOVE:
                showPage();
                upx = event.getX()+offsetx;
                upy = event.getY()+offsety;
                canvas.drawLine(downx, downy, downx, upy, paint);
                canvas.drawLine(downx, downy, upx, downy, paint);
                canvas.drawLine(upx, downy, upx, upy, paint);
                canvas.drawLine(downx, upy, upx, upy, paint);
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX()+offsetx;
                upy = event.getY()+offsety;

                // saving annotation
                AnnotationList.get(currentPage).Rectangles.add(new MyRectangle( new PointF(downx/(float)PAGE_WIDTH ,downy/(float)PAGE_HEIGHT) , new PointF(upx/(float)PAGE_WIDTH,upy/(float)PAGE_HEIGHT)));
                UndoStackList.get(currentPage).push(1);

                SaveAnnotation();     // saving annotation in Database

                canvas.drawLine(downx, downy, downx, upy, paint);
                canvas.drawLine(downx, downy, upx, downy, paint);
                canvas.drawLine(upx, downy, upx, upy, paint);
                canvas.drawLine(downx, upy, upx, upy, paint);
                imageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
    }
    public void textModeAction(View view, MotionEvent event){
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX()+offsetx;
                downy = event.getY()+offsety;

                final Dialog dialog = new Dialog(PdfReaderActivity.this);
                dialog.setContentView(R.layout.insert_text);
                dialog.setTitle("Input Text");
                dialog.show();

                dialog.findViewById(R.id.saveText).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText text = dialog.findViewById(R.id.text);
                        canvas.drawText(text.getText().toString(), downx, downy, paint);
                        dialog.cancel();
                        hideKeyboard(view);

                        // saving annotation
                        AnnotationList.get(currentPage).Texts.add(new MyText(new PointF(downx/(float)PAGE_WIDTH , downy/(float)PAGE_HEIGHT) , text.getText().toString()));
                        UndoStackList.get(currentPage).push(4);

                        SaveAnnotation();     // saving annotation in Database
                    }
                });

                break;

            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
    }
    public void markerModeAction(View view,  MotionEvent event){

        paint.setColor(Color.YELLOW);
        paint.setAlpha(100);
        paint.setStrokeWidth(30);

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX()+offsetx;
                downy = event.getY()+offsety;

                myMarker = new MyMarker();
                myMarker.markers.add(new PointF(downx/(float)PAGE_WIDTH, downy/(float)PAGE_HEIGHT));                 // saving annotation

                break;
            case MotionEvent.ACTION_MOVE:
                upx = event.getX()+offsetx;
                upy = event.getY()+offsety;

                myMarker.markers.add(new PointF(upx/(float)PAGE_WIDTH ,upy/(float)PAGE_HEIGHT));                 // saving annotation

                canvas.drawLine(downx, downy, upx, upy, paint);
                imageView.invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX()+offsetx;
                upy = event.getY()+offsety;

                myMarker.markers.add(new PointF(upx/(float)PAGE_WIDTH, upy/(float)PAGE_HEIGHT));                 // saving annotation
                AnnotationList.get(currentPage).Markers.add(myMarker);                 // saving annotation
                UndoStackList.get(currentPage).push(5);

                SaveAnnotation();     // saving annotation in Database

                canvas.drawLine(downx, downy, upx, upy, paint);
                imageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }

        initPaint();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 10.0f));
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }
    }

    public void showToast(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
    }


}

