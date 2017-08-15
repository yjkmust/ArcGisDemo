package com.yjkmust.arcgisdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.core.geodatabase.ShapefileFeature;
import com.esri.core.geodatabase.ShapefileFeatureTable;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.Renderer;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.esri.core.tasks.SpatialRelationship;
import com.esri.core.tasks.query.QueryParameters;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.yjkmust.arcgisdemo.Adapters.ExPandAdapter;
import com.yjkmust.arcgisdemo.Adapters.ExPandableAdapter;
import com.yjkmust.arcgisdemo.Adapters.LayerVisibilityAdapter;
import com.yjkmust.arcgisdemo.Adapters.QueryResultAdapter;
import com.yjkmust.arcgisdemo.Bean.Attubides;
import com.yjkmust.arcgisdemo.Bean.MapQueryResultModel;
import com.yjkmust.arcgisdemo.Bean.MarkLayerDb;
import com.yjkmust.arcgisdemo.Bean.PipelineModel;
import com.yjkmust.arcgisdemo.Bean.QueryResultModel;
import com.yjkmust.arcgisdemo.Option.GPSOptionClass;
import com.yjkmust.arcgisdemo.Utils.DbUtils;
import com.yjkmust.arcgisdemo.Utils.LayerUtils;
import com.yjkmust.arcgisdemo.Utils.PixelUtils;
import com.yjkmust.arcgisdemo.Utils.Utility;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.yjkmust.arcgisdemo.MapOption.point;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = "MainActivity";
    private MapView mMapView;
    private GraphicsLayer graphicsLayer;
    private GraphicsLayer markLayer;
    final int STATE_ADD_LAYER = 1;//进入添加graphic状态
    final int STATE_SHOW = 2;//选中graphic状态，这是后单击地图操作
    int m_state;//状态
    private int currentDrawGraphicId = -1;
    private int currentDrawGraphicLabelId = -1;
    private Polyline drawPolyline;
    private Polygon drawPolygon;
    private TextView textView;
    private ImageView imageView;
    private LinearLayout linearLayout;
    private LinearLayout llqueryResult;
    private MapOption mapOption = MapOption.nothing;
    private final int SIZE_CLICK_POINT = 30;
    private final SimpleMarkerSymbol SYMBOL_CLICK_POINT =
            new SimpleMarkerSymbol(Color.argb(50, 255, 255, 0), SIZE_CLICK_POINT, SimpleMarkerSymbol.STYLE.CIRCLE);
    private final SimpleLineSymbol SYMBOL_LINE_DEFAULT =
            new SimpleLineSymbol(Color.RED, 3);
    private final SimpleFillSymbol SYMBOL_FILL_DEFAULT =
            new SimpleFillSymbol(Color.argb(100, 255, 255, 0));
    private final SimpleMarkerSymbol SYMBOL_POINT_RED =
            new SimpleMarkerSymbol(Color.argb(255, 255, 0, 0), 5, SimpleMarkerSymbol.STYLE.CIRCLE);
    private final SimpleMarkerSymbol SYMBOL_POINT_DEFAULT =
            new SimpleMarkerSymbol(Color.rgb( 255, 255, 0), 10, SimpleMarkerSymbol.STYLE.CIRCLE);
    private final TextSymbol SYMBOL_TEXT_RED = new TextSymbol(14, "", Color.RED, TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.MIDDLE) {
        {
            setFontFamily("DroidSansFallback.ttf");
            setOffsetX(5);
        }
    };
    private final TextSymbol SYMBOL_TEXT_RED_SMALL = new TextSymbol(12, "", Color.RED, TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.TOP) {
        {
            setFontFamily("DroidSansFallback.ttf");
            setOffsetX(5);
            setOffsetY(-5);
        }
    };
    private DbUtils dbUtils;
    private Button btnClear;
    private SwipeMenuListView listView;
    private ExpandableListView exListView;
    private LinearLayout llqueryResultArea;
    private Button btnClearArea;
    private Point searchPoint;
    private List<FeatureLayer> featureLayers;
    private GraphicsLayer locationLayer;
    private GPSOptionClass gpsOptionClass;
    private ImageView ivLocation;
    private RelativeLayout rlZoom;
    private PopupWindow popupWindow;
    private ImageView ivMeasure;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.tv_display);
        imageView = (ImageView) findViewById(R.id.iv_image);
        ivLocation = (ImageView) findViewById(R.id.iv_location);
        linearLayout = (LinearLayout) findViewById(R.id.ll_content);
        llqueryResult = (LinearLayout) findViewById(R.id.ll_queryResult);
        llqueryResultArea = (LinearLayout) findViewById(R.id.ll_queryResultArea);
        rlZoom = (RelativeLayout) findViewById(R.id.rl_zoom);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnClearArea = (Button) findViewById(R.id.btnClears);
        listView = (SwipeMenuListView) findViewById(R.id.lstView);
        exListView = (ExpandableListView) findViewById(R.id.exListView);
        ivMeasure = (ImageView) findViewById(R.id.iv_measure);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llqueryResult.setVisibility(View.GONE);
                rlZoom.setVisibility(View.VISIBLE);
            }
        });
        btnClearArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llqueryResultArea.setVisibility(View.GONE);
                rlZoom.setVisibility(View.VISIBLE);
            }
        });
        ivMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int x = PixelUtils.getInstance().dp2Px(getResources(),-250);
                int y = PixelUtils.getInstance().dp2Px(getResources(), -75);
                popupWindow.showAsDropDown(ivMeasure,x,y);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbUtils = DbUtils.getDbUtils(this);
        featureLayers = new ArrayList<>();
        initMapView();
        loadMapExtent();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        listView.setMenuCreator(listMenuCreator);
        ivLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLocation){
                    if (!gpsOptionClass.isOpened()){
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        isLocation = false;
                        ivLocation.setImageResource(R.mipmap.img_location_f);
                    }else {
                        gpsOptionClass.start();
                        ivLocation.setImageResource(R.mipmap.img_location_t);
                        isLocation = true;
                    }
                }else {
                    gpsOptionClass.stop();
                    ivLocation.setImageResource(R.mipmap.img_location_f);
                    isLocation = false;
                }
            }
        });
        showPopupWindow();

    }
    SwipeMenuCreator listMenuCreator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            if (menu.getViewType() == 1) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(90);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_clear_black_24dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        }
    };
    private void mapTouch(){
       mMapView.setOnTouchListener(new MapTouchListener(getApplicationContext(),mMapView));
    }
    private class MapTouchListener extends MapOnTouchListener{

        public MapTouchListener(Context context,MapView mapView) {
            super(context, mapView);
        }

        @Override
        public boolean onSingleTap(MotionEvent point) {
            Point curPoint = mMapView.toMapPoint(new Point(point.getX(), point.getY()));
            if (mapOption==MapOption.point){
                if (currentDrawGraphicId > 0) {
                    graphicsLayer.updateGraphic(currentDrawGraphicId, curPoint.copy());
                    Log.d(TAG, "currentDrawGraphicId1: "+currentDrawGraphicId);
                } else {
                    currentDrawGraphicId = graphicsLayer.addGraphic(new Graphic(curPoint.copy(), SYMBOL_POINT_DEFAULT));
                    graphicsLayer.setSelectedGraphics(new int[]{currentDrawGraphicId}, true);
                    Log.d(TAG, "currentDrawGraphicId2: "+currentDrawGraphicId);
                }
            }else if (mapOption==MapOption.line){
                if (currentDrawGraphicId > 0) {
                    Graphic g = graphicsLayer.getGraphic(currentDrawGraphicId);
                    if (g.getGeometry().getType() == Geometry.Type.POINT) {
                        drawPolyline = new Polyline();
                        drawPolyline.startPath((Point) g.getGeometry());
                        drawPolyline.lineTo(curPoint);
                        graphicsLayer.removeGraphic(currentDrawGraphicId);
                        currentDrawGraphicId = graphicsLayer.addGraphic(new Graphic(drawPolyline, SYMBOL_LINE_DEFAULT));
                        graphicsLayer.setSelectedGraphics(new int[]{currentDrawGraphicId}, true);
                    } else {
                        drawPolyline.lineTo(curPoint);
                        graphicsLayer.updateGraphic(currentDrawGraphicId, drawPolyline);
                    }
                } else {
                    currentDrawGraphicId = graphicsLayer.addGraphic(new Graphic(curPoint.copy(), SYMBOL_POINT_DEFAULT));
                    graphicsLayer.setSelectedGraphics(new int[]{currentDrawGraphicId}, true);
                }

            }else if (mapOption==MapOption.recover){
                if (currentDrawGraphicId > 0) {
                    Graphic g = graphicsLayer.getGraphic(currentDrawGraphicId);
                    if (g.getGeometry().getType() == Geometry.Type.POINT) {
                        Polyline line = new Polyline();
                        line.startPath((Point) g.getGeometry().copy());
                        line.lineTo(curPoint);
                        graphicsLayer.removeGraphic(currentDrawGraphicId);
                        currentDrawGraphicId = graphicsLayer.addGraphic(new Graphic(line, SYMBOL_LINE_DEFAULT));
                        graphicsLayer.setSelectedGraphics(new int[]{currentDrawGraphicId}, true);
                    } else if (g.getGeometry().getType() == Geometry.Type.POLYLINE) {
                        Polyline line = (Polyline) g.getGeometry().copy();
                        drawPolygon = new Polygon();
                        drawPolygon.startPath(line.getPoint(0));
                        drawPolygon.lineTo(line.getPoint(1));
                        drawPolygon.lineTo(curPoint);
                        graphicsLayer.removeGraphic(currentDrawGraphicId);
                        currentDrawGraphicId = graphicsLayer.addGraphic(new Graphic(drawPolygon, SYMBOL_FILL_DEFAULT));
                        graphicsLayer.setSelectedGraphics(new int[]{currentDrawGraphicId}, true);
                    } else {
                        drawPolygon.lineTo(curPoint);
                        graphicsLayer.updateGraphic(currentDrawGraphicId, drawPolygon);
                    }
                } else {
                    currentDrawGraphicId = graphicsLayer.addGraphic(new Graphic(curPoint.copy(), SYMBOL_POINT_DEFAULT));
                    graphicsLayer.setSelectedGraphics(new int[]{currentDrawGraphicId}, true);
                }

            }else if (mapOption==MapOption.query){

            }else if(mapOption==MapOption.distance){
                if (currentDrawGraphicId > 0) {
                    Graphic g = graphicsLayer.getGraphic(currentDrawGraphicId);
                    if (g.getGeometry().getType() == Geometry.Type.POINT) {
                        Polyline line = new Polyline();
                        line.startPath((Point) g.getGeometry().copy());
                        line.lineTo(curPoint);
                        //graphicsLayer.removeGraphic(currentDrawGraphicId);
                        //currentDrawGraphicId = graphicsLayer.addGraphic(new Graphic(line, SYMBOL_LINE_DEFAULT));
                        graphicsLayer.updateGraphic(currentDrawGraphicId, new Graphic(line, SYMBOL_LINE_DEFAULT));
                        //添加拐点
                        graphicsLayer.addGraphic(new Graphic(curPoint, SYMBOL_POINT_RED));
                        //添加长度标注
                        TextSymbol textSymbol = SYMBOL_TEXT_RED;
                        textSymbol.setText(Utility.toLengthString(line.calculateLength2D()));
                        graphicsLayer.addGraphic(new Graphic(curPoint, textSymbol));
                        //添加坐标标注
                        TextSymbol textSymbol2 = SYMBOL_TEXT_RED_SMALL;
                        textSymbol2.setText(String.format("X:%.3f Y:%.3f", curPoint.getX(), curPoint.getY()));
                        graphicsLayer.addGraphic(new Graphic(curPoint, textSymbol2));
                    } else {
                        Polyline line = (Polyline) g.getGeometry();
                        line.lineTo(curPoint);
                        graphicsLayer.updateGraphic(currentDrawGraphicId, line);
                        //添加拐点
                        graphicsLayer.addGraphic(new Graphic(curPoint, SYMBOL_POINT_RED));
                        //添加长度标注
                        TextSymbol textSymbol = SYMBOL_TEXT_RED;
                        textSymbol.setText(Utility.toLengthString(line.calculateLength2D()));
                        graphicsLayer.addGraphic(new Graphic(curPoint, textSymbol));
                        //添加坐标标注
                        TextSymbol textSymbol2 = SYMBOL_TEXT_RED_SMALL;
                        textSymbol2.setText(String.format("X:%.3f Y:%.3f", curPoint.getX(), curPoint.getY()));
                        graphicsLayer.addGraphic(new Graphic(curPoint, textSymbol2));
                    }
                } else {
                    currentDrawGraphicId = graphicsLayer.addGraphic(new Graphic(curPoint.copy(), SYMBOL_POINT_DEFAULT));
                    //添加拐点
                    graphicsLayer.addGraphic(new Graphic(curPoint, SYMBOL_POINT_RED));
                    //添加起点标注
                    TextSymbol textSymbol = SYMBOL_TEXT_RED;
                    textSymbol.setText("起点");
                    graphicsLayer.addGraphic(new Graphic(curPoint, textSymbol));
                    //添加坐标标注
                    TextSymbol textSymbol2 = SYMBOL_TEXT_RED_SMALL;
                    textSymbol2.setText(String.format("X:%.3f Y:%.3f", curPoint.getX(), curPoint.getY()));
                    graphicsLayer.addGraphic(new Graphic(curPoint, textSymbol2));
                }

            }else if (mapOption==MapOption.area){
                if (currentDrawGraphicId > 0) {
                    Graphic g = graphicsLayer.getGraphic(currentDrawGraphicId);
                    if (g.getGeometry().getType() == Geometry.Type.POINT) {
                        Polyline line = new Polyline();
                        line.startPath((Point) g.getGeometry().copy());
                        line.lineTo(curPoint);
//                        graphicsLayer.removeGraphic(currentDrawGraphicId);
//                        currentDrawGraphicId = graphicsLayer.addGraphic(new Graphic(line, SYMBOL_LINE_DEFAULT));
                        graphicsLayer.updateGraphic(currentDrawGraphicId, new Graphic(line, SYMBOL_LINE_DEFAULT));
                        //添加拐点
                        graphicsLayer.addGraphic(new Graphic(curPoint, SYMBOL_POINT_RED));
                        //添加坐标标注
                        TextSymbol textSymbol2 = SYMBOL_TEXT_RED_SMALL;
                        textSymbol2.setText(String.format("X:%.3f Y:%.3f", curPoint.getX(), curPoint.getY()));
                        graphicsLayer.addGraphic(new Graphic(curPoint, textSymbol2));
                    } else if (g.getGeometry().getType() == Geometry.Type.POLYLINE) {
                        Polyline line = (Polyline) g.getGeometry();
                        Polygon polygon = new Polygon();
                        polygon.startPath(line.getPoint(0));
                        polygon.lineTo(line.getPoint(1));
                        polygon.lineTo(curPoint);
//                        graphicsLayer.removeGraphic(currentDrawGraphicId);
//                        currentDrawGraphicId = graphicsLayer.addGraphic(new Graphic(polygon, SYMBOL_FILL_DEFAULT));
                        graphicsLayer.updateGraphic(currentDrawGraphicId, new Graphic(polygon, SYMBOL_FILL_DEFAULT));
                        //添加拐点
                        graphicsLayer.addGraphic(new Graphic(curPoint, SYMBOL_POINT_RED));
                        //面积标注
                        TextSymbol textSymbol = SYMBOL_TEXT_RED;
                        textSymbol.setText(Utility.toAreaString(Math.abs(polygon.calculateArea2D())));
                        currentDrawGraphicLabelId = graphicsLayer.addGraphic(new Graphic(curPoint, textSymbol));
                        //添加坐标标注
                        TextSymbol textSymbol2 = SYMBOL_TEXT_RED_SMALL;
                        textSymbol2.setText(String.format("X:%.3f Y:%.3f", curPoint.getX(), curPoint.getY()));
                        graphicsLayer.addGraphic(new Graphic(curPoint, textSymbol2));
                    } else {
                        Polygon polygon = (Polygon) g.getGeometry();
                        polygon.lineTo(curPoint);
                        graphicsLayer.updateGraphic(currentDrawGraphicId, polygon);
                        //添加拐点
                        graphicsLayer.addGraphic(new Graphic(curPoint, SYMBOL_POINT_RED));
                        //面积标注
                        TextSymbol textSymbol = SYMBOL_TEXT_RED;
                        textSymbol.setText(Utility.toAreaString(Math.abs(polygon.calculateArea2D())));
                        graphicsLayer.updateGraphic(currentDrawGraphicLabelId, curPoint);
                        graphicsLayer.updateGraphic(currentDrawGraphicLabelId, textSymbol);
                        //添加坐标标注
                        TextSymbol textSymbol2 = SYMBOL_TEXT_RED_SMALL;
                        textSymbol2.setText(String.format("X:%.3f Y:%.3f", curPoint.getX(), curPoint.getY()));
                        graphicsLayer.addGraphic(new Graphic(curPoint, textSymbol2));
                    }
                } else {
                    currentDrawGraphicId = graphicsLayer.addGraphic(new Graphic(curPoint.copy(), SYMBOL_POINT_DEFAULT));
                    //添加拐点
                    graphicsLayer.addGraphic(new Graphic(curPoint, SYMBOL_POINT_RED));
                    //添加坐标标注
                    TextSymbol textSymbol2 = SYMBOL_TEXT_RED_SMALL;
                    textSymbol2.setText(String.format("X:%.3f Y:%.3f", curPoint.getX(), curPoint.getY()));
                    graphicsLayer.addGraphic(new Graphic(curPoint, textSymbol2));
                }
            }else if (mapOption==MapOption.nothing){
                textView.setText("Nothing");
            }else if(mapOption ==MapOption.queryArea){
                if (currentDrawGraphicId > 0) {
                    graphicsLayer.updateGraphic(currentDrawGraphicId, curPoint.copy());
                } else {
                    currentDrawGraphicId = graphicsLayer.addGraphic(new Graphic(curPoint.copy(), SYMBOL_POINT_RED));
                }
                searchPoint = curPoint;
                Geometry geo = graphicsLayer.getGraphic(currentDrawGraphicId).getGeometry();
                searchPoint(geo);
                llqueryResultArea.setVisibility(View.VISIBLE);
                rlZoom.setVisibility(View.GONE);
            }

            return true;
        }
    }
    public void mapUp(View v){
        mMapView.zoomout();
    }
    public void mapDown(View v){
        mMapView.zoomin();
    }
    private void initExpandListView(){
        List<String> group = new ArrayList<>();
        group.add("11");
        group.add("22");
        group.add("33");
        List<String> items = new ArrayList<>();
        items.add("111");
        items.add("222");
        items.add("333");
        List<String> itemss = new ArrayList<>();
        itemss.add("111");
        itemss.add("222");
        itemss.add("333");
        List<String> itemsss = new ArrayList<>();
        itemsss.add("111");
        itemsss.add("222");
        itemsss.add("333");
        final List item = new ArrayList();
        item.add(items);
        item.add(itemss);
        item.add(itemsss);
        final ExPandAdapter adapter = new ExPandAdapter(group,item);
        exListView.setGroupIndicator(null);//清除默认Indicator
        exListView.setAdapter(adapter);
        //  设置分组项的点击监听事件
        exListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Log.d(TAG, "onGroupClick: groupPosition:" + groupPosition + ", id:" + id);
                boolean groupExpanded = parent.isGroupExpanded(groupPosition);
                adapter.setIndicatorState(groupPosition, groupExpanded);
                // 请务必返回 false，否则分组不会展开
                return false;
            }
        });

        //  设置子选项点击监听事件
        exListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(MainActivity.this, "llll", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_point) {

            // 切换按钮状态，第一次点击本按钮后进入 “添加graphics状态，这时候单击地图时操作就添加graphics”
            // 第一次点击本按钮后进入 “选中graphics状态“，这时候单击地图时操作就
            // 选择一个graphics，并显示该graphics的附加信息”
            textView.setText(getString(R.string.option_map_mark_point));
            LinkBuilder.on(textView)
                    .addLinks(makeLinksForMarker(item))
                    .build();
            mapOption=MapOption.point;
            currentDrawGraphicId = -1;
            mapOption = point;
            linearLayout.setVisibility(View.VISIBLE);
            graphicsLayer.removeAll();
            Toast.makeText(this,"点标注",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_line) {
            currentDrawGraphicId = -1;
            mapOption = MapOption.line;
            imageView.setImageResource(R.drawable.ic_marker_line);
            textView.setText(getString(R.string.option_map_mark_polyline));
            linearLayout.setVisibility(View.VISIBLE);
            LinkBuilder.on(textView)
                    .addLinks(makeLinksForMarker(item))
                    .build();

            graphicsLayer.removeAll();
            Toast.makeText(this,"线标注",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_recover) {
            currentDrawGraphicId = -1;
            mapOption = MapOption.recover;
            textView.setText(getString(R.string.option_map_mark_polygon));
            LinkBuilder.on(textView)
                    .addLinks(makeLinksForMarker(item))
                    .build();
            imageView.setImageResource(R.drawable.ic_marker_polygon);
            linearLayout.setVisibility(View.VISIBLE);
            graphicsLayer.removeAll();
            Toast.makeText(this,"面标注",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_query) {
            currentDrawGraphicId = -1;
            mapOption = MapOption.query;
            textView.setText("查询标注");
            inputMarkKey();
            graphicsLayer.removeAll();
            linearLayout.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ic_marker_clear);
            Toast.makeText(this,"查询标注",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_distance) {
            currentDrawGraphicId = -1;
            mapOption = MapOption.distance;
            textView.setText("距离测量");
            graphicsLayer.removeAll();
            linearLayout.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ic_measure_length);
            Toast.makeText(this,"距离测量",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_area) {
            currentDrawGraphicId = -1;
            mapOption = MapOption.area;
            textView.setText("面积测量");
            graphicsLayer.removeAll();
            linearLayout.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ic_measure_area);
            Toast.makeText(this,"面积测量",Toast.LENGTH_SHORT).show();
        }else if (id == R.id.nav_clear) {
            new AlertDialog.Builder(this, R.style.Dialog_Custom)
                    .setIcon(getResources().getDrawable(R.drawable.ic_help_black_24dp))
                    .setTitle("提示")
                    .setMessage("是否确定要清除所有标注？")
                    .setPositiveButton("清除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            currentDrawGraphicId = -1;
                            mapOption = MapOption.nothing;
                            graphicsLayer.removeAll();
                            markLayer.removeAll();
                            linearLayout.setVisibility(View.GONE);
                            dbUtils.DelAllMarkLayer();
                            Toast.makeText(MainActivity.this,"清除标注",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            item.setChecked(false);
                        }
                    })
                    .create().show();

        }else if (id==R.id.nav_layer){
            ListView listView = new ListView(this);
            listView.setAdapter(new LayerVisibilityAdapter(mMapView));
            new AlertDialog.Builder(this, R.style.Dialog_Custom)
                    .setView(listView)
                    .setTitle("图层控制")
                    .setIcon(getResources().getDrawable(R.drawable.ic_layers_blue_24dp))
                    .create().show();
        }else if (id==R.id.nav_queryArea){
            mapOption = MapOption.queryArea;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void searchPoint(final Geometry geometry){
        final List<QueryResultModel> returnRes = new ArrayList<>();
        final List<List<PipelineModel>> list = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                QueryParameters query = new QueryParameters();
                query.setGeometry(geometry);
                query.setWhere("1=1");
                query.setReturnGeometry(true);
                query.setOutFields(new String[]{"*"});
                query.setSpatialRelationship(SpatialRelationship.INTERSECTS);
                query.setInSpatialReference(mMapView.getSpatialReference());
                for (FeatureLayer layer :featureLayers){
                    if (!layer.isVisible()){
                        continue;
                    }
                    Future<FeatureResult> res = layer.getFeatureTable().queryFeatures(query, null);
                    try {
                        for (Object object : res.get()){
                            ShapefileFeature feature = (ShapefileFeature) object;
                            QueryResultModel result = new QueryResultModel();
                            result.setLayerName(layer.getName());
                            result.setAttributes(feature.getAttributes());
                            result.setFeatureId(feature.getId());
                            result.setGeometry(feature.getGeometry().copy());
                            returnRes.add(result);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                final List<String> layerName = new ArrayList<String>();
                final List<List<Attubides>> layerAttu = new ArrayList<>();
                for (QueryResultModel queryResultModel : returnRes){
                    layerName.add(queryResultModel.getLayerName());
                    Map<String, Object> attributes = queryResultModel.getAttributes();
                    Iterator iter = attributes.entrySet().iterator();
                    List<Attubides> list1 = new ArrayList();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        String key = (String) entry.getKey();
                        Object val =  entry.getValue();
                        Attubides attu = new Attubides();
                        attu.setKey(key);
                        attu.setValue(val);
                        list1.add(attu);
                    }
                    layerAttu.add(list1);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ExPandableAdapter adapters = new ExPandableAdapter(layerName, layerAttu);
                        exListView.setGroupIndicator(null);
                        exListView.setAdapter(adapters);
                    }
                });

            }
        }).start();

    }
    private boolean isLocation = false;
    private void initMapView() {
        m_state = STATE_SHOW;
        mMapView = (MapView) findViewById(R.id.map_view);
        String path="file:///storage/sdcard0/basemap.tpk";
        String paths="file:///storage/sdcard0/YNMRP/汇总数据/基础底图.tpk";
        //声明并实例化ArcGISLocalTiledLayer
        ArcGISLocalTiledLayer localMap=new ArcGISLocalTiledLayer(path);
        //将离线地图加载到MapView中
        localMap.setName("默认地图");
        mMapView.addLayer(localMap);
        graphicsLayer = new GraphicsLayer();
        markLayer = new GraphicsLayer();
        markLayer.setName("标注图层");
        reLaodLayer();
        locationLayer = new GraphicsLayer();
        locationLayer.setName("定位图层");
        gpsOptionClass = new GPSOptionClass(mMapView, locationLayer, GPSOptionClass.GPSProjectType.KM87);
        gpsOptionClass.setMaxLocationScale(3000);
        mMapView.addLayer(markLayer);
        mMapView.addLayer(graphicsLayer);
        mMapView.addLayer(locationLayer);
        addFeatureLayer();
        mapTouch();
    }
    private void addFeatureLayer(){
        LayerUtils layerUtils = new LayerUtils();
        List<String> path = layerUtils.getPath();
        Log.d(TAG, "addFeatureLayer: "+path);
        if (path.size()>0&&path!=null){
            for (String data :path)
                try {
                    File file = new File(data);
                    String name = file.getName().substring(0, file.getName().length() - 4);
                    ShapefileFeatureTable featureTable = new ShapefileFeatureTable(data);
                    FeatureLayer featureLayer = new FeatureLayer(featureTable);
                    featureLayer.setRenderer(getRenderer(featureLayer.getGeometryType()));
                    featureLayer.setName(name);
                    mMapView.addLayer(featureLayer);
                    featureLayers.add(featureLayer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }


    }
    private Renderer getRenderer(Geometry.Type type) {
        if (type == Geometry.Type.ENVELOPE
                || type == Geometry.Type.POLYGON) {
            return new SimpleRenderer(SYMBOL_FILL_DEFAULT);
        } else if (type == Geometry.Type.LINE
                || type == Geometry.Type.POLYLINE) {
            return new SimpleRenderer(SYMBOL_LINE_DEFAULT);
        } else if (type == Geometry.Type.MULTIPOINT
                || type == Geometry.Type.POINT) {
            return new SimpleRenderer(SYMBOL_POINT_DEFAULT);
        } else {
            return null;
        }
    }
    /**
     * 创建标注说明中的link标签
     *
     * @param menuItem
     * @return
     */
    private List<Link> makeLinksForMarker(final MenuItem menuItem) {
        List<Link> links = new ArrayList<>();
        Link link0 = new Link("补充说明")
                .setBold(true)
                .setTextColor(getResources().getColor(R.color.Red))
                .setHighlightAlpha(.4f)
                .setUnderlined(true)
                .setBold(true)
                .setOnClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String clickedText) {
                        if (!hasDrawingMarker()) {
                            new AlertDialog.Builder(MainActivity.this, R.style.Dialog_Custom)
                                    .setIcon(getResources().getDrawable(R.drawable.ic_warning_black_24dp))
                                    .setTitle("提示")
                                    .setMessage("请先绘制标注图形")
                                    .create().show();
                            return;
                        }
                        //输入说明信息
                        inputMarkerLabel();
                    }
                });
        links.add(link0);
        Link link1 = new Link("新增标注")
                .setTextColor(getResources().getColor(R.color.Red))
                .setHighlightAlpha(.4f)
                .setUnderlined(true)
                .setBold(true)
                .setOnClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String clickedText) {
                     resetMarker();
                    }
                });
        links.add(link1);
        Link link = new Link("结束并保存")
                .setTextColor(getResources().getColor(R.color.Red))
                .setHighlightAlpha(.4f)
                .setUnderlined(true)
                .setBold(true)
                .setOnClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String clickedText) {
                        clearMark();
                        SaveMarkers();
                        graphicsLayer.removeAll();
                        reLaodLayer();
                        linearLayout.setVisibility(View.GONE);
                        mapOption = MapOption.nothing;
//                        stopAndSaveMarkers(menuItem);
                    }
                });
        links.add(link);
        return links;
    }
    /**
     * 是否存在当前正在绘制的标注
     *
     * @return
     */
    public boolean hasDrawingMarker() {
        return currentDrawGraphicId > 0 && graphicsLayer.getGraphicIDs() != null && graphicsLayer.getGraphicIDs().length > 0;
    }
    /**
     * 输入标注说明
     */
    private void inputMarkerLabel() {
        final View inputView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.input_string, null);
        final EditText input = (EditText) inputView.findViewById(R.id.text);
        input.setHint("不能超过五十个字");
        input.setText(getCurrentMarkerLabel());
        input.setSelection(input.getText().length());
        final AlertDialog dlg = new AlertDialog.Builder(MainActivity.this, R.style.Dialog_Custom)
                .setIcon(getResources().getDrawable(R.drawable.ic_mode_edit_black_24dp))
                .setTitle("补充说明")
                .setView(inputView)
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .create();
        dlg.show();
        dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String label = input.getText().toString();
                if (label.trim().length() > 50) {
                    input.setError("不能超过五十个字");
                    return;
                }
                setCurrentMarkerLabel(label.trim());
                dlg.dismiss();
            }
        });
        //设置输入框输入后的软键盘监听事件
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    String label = input.getText().toString();
                    if (label.trim().length() > 50) {
                        input.setError("不能超过五十个字");
                    } else {
                       setCurrentMarkerLabel(label.trim());
                        dlg.dismiss();
                    }
                }
                return false;
            }
        });
    }

    /**
     * 获取当前标注的说明
     *
     * @return
     */
    public String getCurrentMarkerLabel() {
        if (currentDrawGraphicId > 0) {
            Graphic g = graphicsLayer.getGraphic(currentDrawGraphicId);
            if (g.getAttributes().containsKey("LabelText")) {
                return String.valueOf(g.getAttributeValue("LabelText"));
            }
        }
        return "";
    }
    /**
     * 设置当前标注的说明
     *
     * @param label
     */
    public void setCurrentMarkerLabel(String label) {
        if (currentDrawGraphicId > 0) {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("LabelText", label);
            graphicsLayer.updateGraphic(currentDrawGraphicId, attributes);
            if (currentDrawGraphicLabelId > 0) {
                TextSymbol textSymbol = new TextSymbol(14, label, Color.BLACK, TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.MIDDLE);
                textSymbol.setFontFamily("DroidSansFallback.ttf");
                textSymbol.setOffsetX(5);
//                TextSymbol textSymbol = (TextSymbol) graphicsLayer.getGraphic(currentDrawGraphicLabelId).getSymbol();
//                textSymbol.setText(label);
                graphicsLayer.updateGraphic(currentDrawGraphicLabelId, textSymbol);
            } else {
                if (label != null && label.length() > 0) {
                    TextSymbol textSymbol = new TextSymbol(14, label, Color.BLACK, TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.MIDDLE);
                    textSymbol.setFontFamily("DroidSansFallback.ttf");
                    textSymbol.setOffsetX(5);
                    Geometry labelPoint = null;
                    if (graphicsLayer.getGraphic(currentDrawGraphicId).getGeometry() instanceof MultiPath) {
                        labelPoint = ((MultiPath) graphicsLayer.getGraphic(currentDrawGraphicId).getGeometry()).getPoint(0).copy();
                    } else if (graphicsLayer.getGraphic(currentDrawGraphicId).getGeometry() instanceof Point) {
                        labelPoint = ((Point) graphicsLayer.getGraphic(currentDrawGraphicId).getGeometry()).copy();
                    } else {
                        return;
                    }
                    currentDrawGraphicLabelId = graphicsLayer.addGraphic(new Graphic(labelPoint, textSymbol));
                }
            }
        }
    }
    /**
     * 重置标注，重置后相当于添加新的标注
     */
    public void resetMarker() {
        currentDrawGraphicId = -1;
        currentDrawGraphicLabelId = -1;
        graphicsLayer.clearSelection();
    }
    /**
     * 保存标注数据
     */
    public void  SaveMarkers(){
        List<Graphic> graphics = new ArrayList<>();
        for (int id : graphicsLayer.getGraphicIDs()){
           if (graphicsLayer.getGraphic(id).getSymbol() instanceof TextSymbol){
               continue;
           }
            graphics.add(graphicsLayer.getGraphic(id));
        }
        for (Graphic graphic : graphics){
            MarkLayerDb db = new MarkLayerDb();
            Object labelText = graphic.getAttributes().containsKey("LabelText") ? graphic.getAttributes().get("LabelText") : "";
            Object shapeJson = GeometryEngine.geometryToJson(graphic.getSpatialReference(), graphic.getGeometry());
            db.setLabelText((String) labelText);
            db.setShapeJson((String) shapeJson);
            dbUtils.insertMarkLayer(db);
        }
    }
    /**
     * 获取数据库Layer
     */
    private void reLaodLayer(){
        if (markLayer==null){
            return;
        }
        markLayer.removeAll();
        List<MarkLayerDb> list = dbUtils.loadAllMarkLayer();
        for (MarkLayerDb db : list){
            if (db.getData2()!=null&&!db.getData2().isEmpty()){
                if (db.getData2().equals("地图视野"))
                continue;
            }
            JsonFactory jsonFactory = new JsonFactory();
            JsonParser jsonParser = null;
            try {
                jsonParser = jsonFactory.createJsonParser(db.getShapeJson());
            }catch (IOException e){
                e.printStackTrace();
                continue;
            }
            MapGeometry mapGeometry = GeometryEngine.jsonToGeometry(jsonParser);
            Geometry geometry = mapGeometry.getGeometry();
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("ID", db.getID());
            attributes.put("LabelText", db.getLabelText());
            Graphic graphic = null;
            Geometry labelPoint = null;
            if (geometry.getType()==Geometry.Type.POINT){
                graphic = new Graphic(geometry, SYMBOL_POINT_DEFAULT, attributes);
                labelPoint = geometry.copy();
            }else if (geometry.getType() == Geometry.Type.POLYLINE){
                graphic = new Graphic(geometry, SYMBOL_LINE_DEFAULT, attributes);
                labelPoint = ((MultiPath)geometry).getPoint(0).copy();
            } else if (geometry.getType() == Geometry.Type.POLYGON){
                graphic = new Graphic(geometry, SYMBOL_FILL_DEFAULT, attributes);
                labelPoint = ((MultiPath)geometry).getPoint(0).copy();
            }else{
                continue;
            }
            int graphicId = markLayer.addGraphic(graphic);
            if (db.getLabelText() != null && db.getLabelText().length() > 0){
                TextSymbol textSymbol = new TextSymbol(14, db.getLabelText(), Color.BLACK, TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.MIDDLE);
                textSymbol.setFontFamily("DroidSansFallback.ttf");
                textSymbol.setOffsetX(5);
                Map<String, Object> attrs = new HashMap<>();
                attrs.put("GID", graphicId);
                Graphic labelGraphic = new Graphic(labelPoint, textSymbol, attrs);
                markLayer.addGraphic(labelGraphic);
            }
        }
    }
    private void clearMark(){
        currentDrawGraphicId = -1;
        currentDrawGraphicLabelId= -1;
    }
    /**
     * 查询标注
     */
    /**
     * 输入标注查询关键字
     */
    private void inputMarkKey() {
        final View inputView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.auto_input_string, null);
        final AutoCompleteTextView input = (AutoCompleteTextView) inputView.findViewById(R.id.text);
        input.setHint("请输入标注关键字");
        final AlertDialog dlg = new AlertDialog.Builder(MainActivity.this, R.style.Dialog_Custom)
                .setIcon(getResources().getDrawable(R.drawable.ic_mode_edit_black_24dp))
                .setTitle("查询标注")
                .setView(inputView)
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .create();
        dlg.show();
        dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String label = input.getText().toString();
                if (label.trim().length() <= 0) {
                    input.setError("不能为空");
                    return;
                }
                queryMarker(label.trim());
                dlg.dismiss();
            }
        });
        //设置输入框输入后的软键盘监听事件
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    String label = input.getText().toString();
                    if (label.trim().length() <= 0) {
                        input.setError("不能为空");
                    } else {
                        dlg.dismiss();
                    }
                }
                return false;
            }
        });
    }
    private void queryMarker(final String key){
        if (!markLayer.isVisible()) {
            markLayer.setVisible(true);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                final List<MapQueryResultModel> returnRes = new ArrayList<>();
                if (markLayer.getGraphicIDs() != null) {
                    for (int id : markLayer.getGraphicIDs()) {
                        String label = markLayer.getGraphic(id).getAttributeValue("LabelText") != null
                                ? markLayer.getGraphic(id).getAttributeValue("LabelText").toString()
                                : "";
                        if (label.length() > 0 && label.contains(key)) {
                            MapQueryResultModel item = new MapQueryResultModel();
                            item.setIndex(index);
                            item.setText(label);
                            item.setValue(id);
                            returnRes.add(item);
                            index++;
                        }
                    }
                }
               runOnUiThread(new Runnable() {
                   private QueryResultAdapter adapter;

                   @Override
                   public void run() {
                     if (returnRes.size()>0&&returnRes!=null){
                         llqueryResult.setVisibility(View.VISIBLE);
                         rlZoom.setVisibility(View.GONE);
                         markLayer.clearSelection();
                         adapter = new QueryResultAdapter(returnRes, true);
                         listView.setAdapter(adapter);
                         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                             @Override
                             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                 MapQueryResultModel selectItem = (MapQueryResultModel) adapter.getItem(i);
                                 selectMarkerGraphic((int) selectItem.getValue());
                             }
                         });
                         listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                             @Override
                             public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                                 final MapQueryResultModel model = (MapQueryResultModel) adapter.getItem(position);
                                 switch (index){
                                     case 0:
                                         new AlertDialog.Builder(MainActivity.this, R.style.Dialog_Custom)
                                                 .setIcon(getResources().getDrawable(R.drawable.ic_help_black_24dp))
                                                 .setTitle("提示")
                                                 .setMessage("是否要删除该标注？")
                                                 .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialogInterface, int i) {
                                                        deleteMarker(new int[]{(Integer) model.getValue()});
                                                         adapter.deleteItem(position);
                                                     }
                                                 })
                                                 .setNegativeButton("否", null)
                                                 .create().show();
                                         break;
                                     default:
                                         break;

                                 }
                                 return false;
                             }
                         });

                     }else {
                         Toast.makeText(MainActivity.this,"没有相关的标注记录",Toast.LENGTH_SHORT).show();
                     }
                   }
               });
            }
        }).start();
    }
    private void selectMarkerGraphic(int id){
        markLayer.clearSelection();
        if (markLayer.getGraphic(id)!=null){
            markLayer.setSelectedGraphics(new int[]{id},true);
            Envelope envelope = new Envelope();
            markLayer.getGraphic(id).getGeometry().queryEnvelope(envelope);
            mMapView.setExtent(envelope);
        }
    }
    /**
     * 删除标注
     *
     * @param ids
     */
    public void deleteMarker(int[] ids) {
        if (ids == null) {
            return;
        }
        List<Long> lstId = new ArrayList<>();
        for (int id : ids) {
            lstId.add((Long) markLayer.getGraphic(id).getAttributeValue("ID"));
        }
        dbUtils.deleteOnes(lstId);
//        new LayerService().deleteMarker(lstId);
        markLayer.removeGraphics(ids);
        if (markLayer.getGraphicIDs() != null) {
            for (int id : markLayer.getGraphicIDs()) {
                Graphic g = markLayer.getGraphic(id);
                if (g.getAttributes().containsKey("GID")) {
                    for (int gid : ids) {
                        if (gid == (int) g.getAttributeValue("GID")) {
                            markLayer.removeGraphic(id);
                        }
                    }
                }
            }
        }
    }
    /**
     * 保存地图视野
     */
    public void saveMapExtent() {

            Geometry geo = mMapView.getExtent();
            if (geo != null && geo.isValid() && !geo.isEmpty()) {
                final String json = GeometryEngine.geometryToJson(mMapView.getSpatialReference(), geo);
                MarkLayerDb markLayerDb = new MarkLayerDb();
                markLayerDb.setData1(json);
                markLayerDb.setData2("地图视野");
                dbUtils.deleteMap("地图视野");
                dbUtils.insertMarkLayer(markLayerDb);
            }
    }
    /**
     * 加载地图视野
     */
    public void loadMapExtent() {
        List<MarkLayerDb> list = dbUtils.QureyBuilderByMap("地图视野");
        if (list.size()>0&&list!=null){
            String jsonExtent = list.get(0).getData1();
            if (jsonExtent != null && jsonExtent.length() > 0) {
                Geometry geo = Utility.json2Geometry(jsonExtent);
                if (geo != null && geo.isValid() && !geo.isEmpty()) {
                    mMapView.setExtent(geo);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this, R.style.Dialog_Custom)
                    .setTitle("提示")
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setMessage("确定要退出应用吗？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //保存当前视野
                            saveMapExtent();
                            finish();
                        }
                    })
                    .setNegativeButton("否", null)
                    .create().show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void showPopupWindow(){
        View popView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popupwindow_item,null);
        int spinnerWeight = PixelUtils.getInstance().dp2Px(getResources(), 250);
        popupWindow = new PopupWindow(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(popView);
        popupWindow.setWidth(spinnerWeight);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        LinearLayout llLength = popView.findViewById(R.id.ll_measure_length);
        LinearLayout llArea = popView.findViewById(R.id.ll_measure_area);
        llLength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        llArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }
}
