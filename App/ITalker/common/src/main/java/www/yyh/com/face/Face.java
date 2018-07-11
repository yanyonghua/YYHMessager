package www.yyh.com.face;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import www.yyh.com.common.R;
import www.yyh.com.utils.StreamUtil;

/**
 * 表情工具类
 * Created by 56357 on 2018/7/9
 */
public class Face {

    //全局的表情的映射ArrayMap，更加轻量级
   private static final ArrayMap<String ,Bean> FACE_MAP=new ArrayMap<>();

    private static List<FaceTab> FACE_TABS=null;

    private static void  init(Context context){
        if (FACE_TABS==null){
            synchronized (Face.class){
                if (FACE_TABS==null){
                    ArrayList<FaceTab> faceTabs =new ArrayList<>();
                    FaceTab tab = initAssetsFace(context);
                    if (tab!=null)
                        faceTabs.add(tab);

                    tab =initResourceFace(context);
                    if (tab!=null)
                        faceTabs.add(tab);
                    // init map
                    for (FaceTab faceTab : faceTabs) {
                        faceTab.copyToMap(FACE_MAP);
                    }

                    //init list 不可变集合
                    FACE_TABS = Collections.unmodifiableList(faceTabs);

                }
            }
        }
    }

    // 从face_t.zip
    private static FaceTab initAssetsFace(Context context) {
        String faceAsset="face-t.zip";
        // data/data/包名//face/tf/*
        String faceCacheDir=String.format("%s/face/tf",context.getFilesDir());
        Log.e("FACE", "initAssetsFace: " +faceCacheDir);
        File faceFolder =new File(faceCacheDir);
        if (!faceFolder.exists()){
           if ( faceFolder.mkdirs()){
               try {
                   InputStream inputStream =context.getAssets().open(faceAsset);
                   //存储文件
                   File faceSource =new File (faceFolder,"source.zip");
                   //copy
                   StreamUtil.copy(inputStream,faceSource);
                   // 解压
                   unZipFile(faceSource,faceFolder);
                   //清理文件
                   StreamUtil.delete(faceSource.getAbsolutePath());
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        }
        //info.json
        File infoFile =new File (faceCacheDir,"info.json");
        Gson gson =new Gson();
        JsonReader reader ;
        try {
            reader=gson.newJsonReader(new FileReader(infoFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        //解析
        FaceTab tab = gson.fromJson(reader,FaceTab.class);

        //相对路径到绝对路径
        for (Bean face : tab.faces) {
            face.preview =String.format("%s/%s",faceCacheDir,face.preview);
            face.source =String.format("%s/%s",faceCacheDir,face.source);
        }

        return tab;
    }

    //把zipFile解压到desDir目录
    private static void unZipFile(File zipFile, File desDir) throws IOException {
        final String folderPath = desDir.getAbsolutePath();
        ZipFile  zf=new ZipFile(zipFile);
        //判断节点进行循环
        for (Enumeration<?> entries=zf.entries();entries.hasMoreElements();){
            ZipEntry entry = (ZipEntry) entries.nextElement();
            //过滤缓存的文件
            String name=entry.getName();
            if (name.startsWith("."))continue;
            //输入流
            InputStream in=zf.getInputStream(entry);
            String str =folderPath+File.separator+name;

            //防止名字错乱
            str =new String(str.getBytes("8859_1"),"GB2312" );

            File desFile=new File(str);
            //输出文件
            StreamUtil.copy(in,desFile);
        }
    }

    // 从Drawable 资源中加载数据并映射到对应的key
    private static FaceTab initResourceFace(Context context) {
     final ArrayList<Bean> faces =new ArrayList<>();
     final Resources resources= context.getResources();
     String packageName=context.getApplicationInfo().packageName;
     for (int i=1;i<142;i++){
         // i=1=>001
         String key =String.format(Locale.ENGLISH,"fb%03d",i);
         String reStr = String.format(Locale.ENGLISH,"face_base_%03d",i);

         //根据资源名称去哪资源对应的ID
         int resId = resources.getIdentifier(reStr,"drawable",packageName);
         if (resId==0)continue;

         //添加表情
         faces.add(new Bean(key,resId));

     }
     if (faces.size()==0)
         return null;


     return new FaceTab("NAME",faces.get(0).preview,faces);
    }

    //获取所有的表情
    public static List<FaceTab> all(@NonNull Context context){
        init(context);
        return FACE_TABS;
    }
    //输入表情到editable
    public static void inputFace(@NonNull final Context context, final Editable editable,
                                 final Face.Bean bean, final int size){
        Glide.with(context)
                .load(bean.preview)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(size ,size) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Spannable spannable =new SpannableString(String.format("[%s]",bean.key));
                        ImageSpan span =new ImageSpan(context,resource,ImageSpan.ALIGN_BASELINE);
                        //属性前后不关联
                        spannable.setSpan(span,0,spannable.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        editable.append(spannable);
                    }
                });
    }

    public static Bean get(Context context ,String key){
        init(context);
        if (FACE_MAP.containsKey(key)){
            return FACE_MAP.get(key);
        }
        return null;
    }
    //从spannable解析表情并替换显示
    public static Spannable decode(@NonNull View target, final Spannable spannable, final int size){
        if (spannable==null)
            return null;
        String str=spannable.toString();
        if (TextUtils.isEmpty(str))
            return null;
        final  Context context =target.getContext();
        // 进行正在匹配[][][]
        Pattern pattern = Pattern.compile("(\\[[^\\[\\]:\\s\\n]+\\])");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()){
            String key =matcher.group();
            if (TextUtils.isEmpty(key))continue;
            Bean bean = get(context,key.replace("[","").replace("]",""));
            if (bean==null)
                continue;
            final int start =matcher.start();
            final int end =matcher.end();
            ImageSpan span =new FaceSpan(context,target,bean.preview,size);
            spannable.setSpan(span,start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        return null;
    }

    public static class  FaceSpan extends ImageSpan{

        //自己真实绘制的
        private Drawable mDrawable;
        private View mView;
        private int mSize;
        /**
         * 构造
         * @param context 上下文
         * @param view 目标View，用于加载完成是刷新
         * @param source 加载目标
         * @param size 图片显示的大小
         */
        public FaceSpan(Context context,View view,Object source,final int size) {

            //虽然设置了默认的表情，但是并不显示仅仅是占位用
            super(context, R.drawable.default_face, ALIGN_BOTTOM);
            mView=view;
            mSize=size;
            Glide.with(context)
                    .load(source)
                    .fitCenter()
                    .into(new SimpleTarget<GlideDrawable>(size ,size) {

                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {

                            mDrawable =resource.getCurrent();
                            //获取自测量高宽
                          int width=  mDrawable.getIntrinsicWidth();
                          int height=  mDrawable.getIntrinsicHeight();

                          mDrawable.setBounds(0,0,width>0?width:size,height>0?height:size);
                          //这边重写刷新界面
                          mView.invalidate();
                        }
                    });
        }

        @Override
        public Drawable getDrawable() {
            //复写那Drawable的方法，当然这里有可能返回的是null
            return mDrawable;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            if (mDrawable!=null)
            super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {

            //走我们自己的逻辑，进行测量
            Rect rect = mDrawable!=null?mDrawable.getBounds():
                    new Rect(0,0,mSize,mSize);

            if (fm != null) {
                fm.ascent = -rect.bottom;
                fm.descent = 0;

                fm.top = fm.ascent;
                fm.bottom = 0;
            }
            return rect.right;
        }
    }


    /**
     * 每一个表情盘含有很多表情
     */
    public static class FaceTab{
        public List<Bean> faces =new ArrayList<>();
        public String name;
        //预览图，包括了drawable下面的资源int类型
        public Object preview;

        public void copyToMap(ArrayMap<String, Bean> faceMap) {
            for (Bean face : faces) {
                faceMap.put(face.key,face);
            }

        }

        public FaceTab( String name, Object preview,List<Bean> faces) {
            this.faces = faces;
            this.name = name;
            this.preview = preview;
        }
    }
    /**
     * 每一个表情
     */
    public static class Bean{
        Bean(String key,int preview){
            this.key = key;
            this.source = preview;
            this.preview = preview;
        }
        public  String key;//
        public  Object source;//源文件
        public  Object preview;//预览
        public  String desc;//描述
        public String toString() {
            return "Bean{" +
                    "key=" + key +
                    ", source='" + source + '\'' +
                    ", desc='" + desc + '\'' +
                    ", preview=" + preview +
                    '}';
        }
    }

}
