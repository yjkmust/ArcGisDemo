package com.yjkmust.arcgisdemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.yjkmust.arcgisdemo.Bean.MapQueryResultModel;
import com.yjkmust.arcgisdemo.Bean.MarkLayerDb;
import com.yjkmust.arcgisdemo.Utils.DbUtils;
import com.yjkmust.arcgisdemo.Utils.Utility;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.tv_display);
        imageView = (ImageView) findViewById(R.id.iv_image);
        linearLayout = (LinearLayout) findViewById(R.id.ll_content);
        llqueryResult = (LinearLayout) findViewById(R.id.ll_queryResult);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llqueryResult.setVisibility(View.GONE);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbUtils = DbUtils.getDbUtils(this);
        initMapView();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
    }
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
            }

            return true;
        }
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
    public boolean onNavigationItemSelected(MenuItem item) {
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
            Toast.makeText(this,"1111",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this,"1111",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this,"1111",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_query) {
            currentDrawGraphicId = -1;
            mapOption = MapOption.query;
            textView.setText("查询标注");
            inputMarkKey();
            graphicsLayer.removeAll();
            linearLayout.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ic_marker_clear);
            Toast.makeText(this,"1111",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_distance) {
            currentDrawGraphicId = -1;
            mapOption = MapOption.distance;
            textView.setText("距离测量");
            graphicsLayer.removeAll();
            linearLayout.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ic_measure_length);
            Toast.makeText(this,"1111",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_area) {
            currentDrawGraphicId = -1;
            mapOption = MapOption.area;
            textView.setText("面积测量");
            graphicsLayer.removeAll();
            linearLayout.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ic_measure_area);
            Toast.makeText(this,"1111",Toast.LENGTH_SHORT).show();
        }else if (id == R.id.nav_clear) {
            currentDrawGraphicId = -1;
            mapOption = MapOption.nothing;
            graphicsLayer.removeAll();
            markLayer.removeAll();
            linearLayout.setVisibility(View.GONE);
            dbUtils.DelAllMarkLayer();
            Toast.makeText(this,"1111",Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void initMapView() {
        m_state = STATE_SHOW;
        mMapView = (MapView) findViewById(R.id.map_view);
        String path="file:///storage/sdcard0/basemap.tpk";
        //声明并实例化ArcGISLocalTiledLayer
        ArcGISLocalTiledLayer localMap=new ArcGISLocalTiledLayer(path);
        //将离线地图加载到MapView中
        mMapView.addLayer(localMap);
        graphicsLayer = new GraphicsLayer();
        markLayer = new GraphicsLayer();
        reLaodLayer();
        mMapView.addLayer(markLayer);
        mMapView.addLayer(graphicsLayer);
        mapTouch();
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
                   @Override
                   public void run() {
                     if (returnRes.size()>0&&returnRes!=null){
                         llqueryResult.setVisibility(View.VISIBLE);
                     }else {
                         Toast.makeText(MainActivity.this,"没有相关的标注记录",Toast.LENGTH_SHORT).show();
                     }
                   }
               });
            }
        }).start();
    }

}
