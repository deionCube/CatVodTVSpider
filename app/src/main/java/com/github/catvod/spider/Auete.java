package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Auete extends Spider {
    private static final String siteUrl = "https://auete.com";
    private static final String siteHost = "auete.com";

    /**
     * 播放源配置
     */
    private JSONObject playerConfig;
    /**
     * 筛选配置
     */
    private JSONObject filterConfig;
    //分类页URL规则
    private Pattern regexCategory = Pattern.compile("/(\\w+)/index.html");
    //详情页URL规则
    private Pattern regexVid = Pattern.compile("/(\\w+/\\w+/\\w+)/");
    //播放页URL规则
    private Pattern regexPlay = Pattern.compile("(/\\w+/\\w+/\\w+/play-\\d+-\\d+.html)");
    //筛选页URL规则
    //private Pattern regexPage = Pattern.compile("/\\w+/\\w+/index(\\d+).html");
    private Pattern regexPage = Pattern.compile("/index(\\d+).html");

    @Override
    public void init(Context context) {
        super.init(context);
        try {
            playerConfig = new JSONObject("{\"dphd\":{\"sh\":\"云播A线\",\"pu\":\"\",\"sn\":0,\"or\":999},\"byun\":{\"sh\":\"云播B线\",\"pu\":\"\",\"sn\":0,\"or\":999},\"ccyun\":{\"sh\":\"云播C线\",\"pu\":\"\",\"sn\":0,\"or\":999},\"dbm3u8\":{\"sh\":\"云播D线\",\"pu\":\"\",\"sn\":0,\"or\":999},\"i8i\":{\"sh\":\"云播E线\",\"pu\":\"\",\"sn\":0,\"or\":999},\"m3u8hd\":{\"sh\":\"云播F线\",\"pu\":\"https://auete.com/api/?url=\",\"sn\":1,\"or\":999},\"languang\":{\"sh\":\"云播G线\",\"pu\":\"https://auete.com/api/?url=\",\"sn\":1,\"or\":999},\"hyun\":{\"sh\":\"云播H线\",\"pu\":\"https://auete.com/api/?url=\",\"sn\":1,\"or\":999},\"kyun\":{\"sh\":\"云播K线\",\"pu\":\"https://auete.com/api/?url=\",\"sn\":1,\"or\":999},\"lyun\":{\"sh\":\"云播L线\",\"pu\":\"https://auete.com/api/?url=\",\"sn\":1,\"or\":999},\"myun\":{\"sh\":\"云播M线\",\"pu\":\"\",\"sn\":0,\"or\":999},\"oyun\":{\"sh\":\"云播O线\",\"pu\":\"\",\"sn\":0,\"or\":999},\"dp\":{\"sh\":\"Dplayer\",\"pu\":\"\",\"sn\":0,\"or\":999},\"bpyueyu\":{\"sh\":\"云播粤语\",\"pu\":\"\",\"sn\":0,\"or\":999},\"bpguoyu\":{\"sh\":\"云播国语\",\"pu\":\"\",\"sn\":0,\"or\":999}}");
            filterConfig = new JSONObject("{\"Movie\":[{\"key\":0,\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"喜剧片\",\"v\":\"xjp\"},{\"n\":\"动作片\",\"v\":\"dzp\"},{\"n\":\"爱情片\",\"v\":\"aqp\"},{\"n\":\"科幻片\",\"v\":\"khp\"},{\"n\":\"恐怖片\",\"v\":\"kbp\"},{\"n\":\"惊悚片\",\"v\":\"jsp\"},{\"n\":\"战争片\",\"v\":\"zzp\"},{\"n\":\"剧情片\",\"v\":\"jqp\"}]},{\"key\":11,\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"},{\"n\":\"more\",\"v\":\"more\"}]},{\"key\":3,\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"Netflix\",\"v\":\"Netflix\"},{\"n\":\"惊悚\",\"v\":\"惊悚\"},{\"n\":\"悬疑\",\"v\":\"悬疑\"},{\"n\":\"Top250\",\"v\":\"Top250\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"漫改\",\"v\":\"漫改\"},{\"n\":\"灾难\",\"v\":\"灾难\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"文艺\",\"v\":\"文艺\"},{\"n\":\"西部\",\"v\":\"西部\"},{\"n\":\"丧尸\",\"v\":\"丧尸\"},{\"n\":\"传记\",\"v\":\"传记\"},{\"n\":\"经典\",\"v\":\"经典\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"音乐\",\"v\":\"音乐\"},{\"n\":\"穿越\",\"v\":\"穿越\"},{\"n\":\"武侠\",\"v\":\"武侠\"},{\"n\":\"歌舞\",\"v\":\"歌舞\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"名著\",\"v\":\"名著\"},{\"n\":\"同性\",\"v\":\"同性\"},{\"n\":\"运动\",\"v\":\"运动\"},{\"n\":\"邵氏\",\"v\":\"邵氏\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"短片\",\"v\":\"短片\"},{\"n\":\"儿童\",\"v\":\"儿童\"},{\"n\":\"恐怖\",\"v\":\"恐怖\"},{\"n\":\"二次元\",\"v\":\"二次元\"},{\"n\":\"商战\",\"v\":\"商战\"}]},{\"key\":1\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"马来西亚\",\"v\":\"马来西亚\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":2,\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hit\"},{\"n\":\"推荐\",\"v\":\"commend\"}]}],\"Tv\":[{\"key\":0,\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"美剧\",\"v\":\"oumei\"},{\"n\":\"韩剧\",\"v\":\"hanju\"},{\"n\":\"日剧\",\"v\":\"riju\"},{\"n\":\"泰剧\",\"v\":\"yataiju\"},{\"n\":\"网剧\",\"v\":\"wangju\"},{\"n\":\"台剧\",\"v\":\"taiju\"},{\"n\":\"国产\",\"v\":\"neidi\"},{\"n\":\"港剧\",\"v\":\"tvbgj\"}]},{\"key\":11,\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"},{\"n\":\"more\",\"v\":\"more\"}]},{\"key\":3,\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"Netflix\",\"v\":\"Netflix\"},{\"n\":\"惊悚\",\"v\":\"惊悚\"},{\"n\":\"悬疑\",\"v\":\"悬疑\"},{\"n\":\"Top250\",\"v\":\"Top250\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"漫改\",\"v\":\"漫改\"},{\"n\":\"灾难\",\"v\":\"灾难\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"文艺\",\"v\":\"文艺\"},{\"n\":\"西部\",\"v\":\"西部\"},{\"n\":\"丧尸\",\"v\":\"丧尸\"},{\"n\":\"传记\",\"v\":\"传记\"},{\"n\":\"经典\",\"v\":\"经典\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"音乐\",\"v\":\"音乐\"},{\"n\":\"穿越\",\"v\":\"穿越\"},{\"n\":\"武侠\",\"v\":\"武侠\"},{\"n\":\"歌舞\",\"v\":\"歌舞\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"名著\",\"v\":\"名著\"},{\"n\":\"同性\",\"v\":\"同性\"},{\"n\":\"运动\",\"v\":\"运动\"},{\"n\":\"邵氏\",\"v\":\"邵氏\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"短片\",\"v\":\"短片\"},{\"n\":\"儿童\",\"v\":\"儿童\"},{\"n\":\"恐怖\",\"v\":\"恐怖\"},{\"n\":\"二次元\",\"v\":\"二次元\"},{\"n\":\"商战\",\"v\":\"商战\"}]},{\"key\":1\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"马来西亚\",\"v\":\"马来西亚\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":2,\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hit\"},{\"n\":\"推荐\",\"v\":\"commend\"}]}],\"Zy\":[{\"key\":0,\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"国综\",\"v\":\"guozong\"},{\"n\":\"韩综\",\"v\":\"hanzong\"},{\"n\":\"美综\",\"v\":\"meizong\"}]},{\"key\":11,\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"},{\"n\":\"more\",\"v\":\"more\"}]},{\"key\":3,\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"Netflix\",\"v\":\"Netflix\"},{\"n\":\"惊悚\",\"v\":\"惊悚\"},{\"n\":\"悬疑\",\"v\":\"悬疑\"},{\"n\":\"Top250\",\"v\":\"Top250\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"漫改\",\"v\":\"漫改\"},{\"n\":\"灾难\",\"v\":\"灾难\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"文艺\",\"v\":\"文艺\"},{\"n\":\"西部\",\"v\":\"西部\"},{\"n\":\"丧尸\",\"v\":\"丧尸\"},{\"n\":\"传记\",\"v\":\"传记\"},{\"n\":\"经典\",\"v\":\"经典\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"音乐\",\"v\":\"音乐\"},{\"n\":\"穿越\",\"v\":\"穿越\"},{\"n\":\"武侠\",\"v\":\"武侠\"},{\"n\":\"歌舞\",\"v\":\"歌舞\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"名著\",\"v\":\"名著\"},{\"n\":\"同性\",\"v\":\"同性\"},{\"n\":\"运动\",\"v\":\"运动\"},{\"n\":\"邵氏\",\"v\":\"邵氏\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"短片\",\"v\":\"短片\"},{\"n\":\"儿童\",\"v\":\"儿童\"},{\"n\":\"恐怖\",\"v\":\"恐怖\"},{\"n\":\"二次元\",\"v\":\"二次元\"},{\"n\":\"商战\",\"v\":\"商战\"}]},{\"key\":1\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"马来西亚\",\"v\":\"马来西亚\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":2,\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hit\"},{\"n\":\"推荐\",\"v\":\"commend\"}]}],\"Dm\":[{\"key\":0,\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"动画\",\"v\":\"donghua\"},{\"n\":\"日漫\",\"v\":\"riman\"},{\"n\":\"国漫\",\"v\":\"guoman\"},{\"n\":\"美漫\",\"v\":\"meiman\"}]},{\"key\":11,\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"},{\"n\":\"more\",\"v\":\"more\"}]},{\"key\":3,\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"Netflix\",\"v\":\"Netflix\"},{\"n\":\"惊悚\",\"v\":\"惊悚\"},{\"n\":\"悬疑\",\"v\":\"悬疑\"},{\"n\":\"Top250\",\"v\":\"Top250\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"漫改\",\"v\":\"漫改\"},{\"n\":\"灾难\",\"v\":\"灾难\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"文艺\",\"v\":\"文艺\"},{\"n\":\"西部\",\"v\":\"西部\"},{\"n\":\"丧尸\",\"v\":\"丧尸\"},{\"n\":\"传记\",\"v\":\"传记\"},{\"n\":\"经典\",\"v\":\"经典\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"音乐\",\"v\":\"音乐\"},{\"n\":\"穿越\",\"v\":\"穿越\"},{\"n\":\"武侠\",\"v\":\"武侠\"},{\"n\":\"歌舞\",\"v\":\"歌舞\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"名著\",\"v\":\"名著\"},{\"n\":\"同性\",\"v\":\"同性\"},{\"n\":\"运动\",\"v\":\"运动\"},{\"n\":\"邵氏\",\"v\":\"邵氏\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"短片\",\"v\":\"短片\"},{\"n\":\"儿童\",\"v\":\"儿童\"},{\"n\":\"恐怖\",\"v\":\"恐怖\"},{\"n\":\"二次元\",\"v\":\"二次元\"},{\"n\":\"商战\",\"v\":\"商战\"}]},{\"key\":1\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"马来西亚\",\"v\":\"马来西亚\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":2,\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hit\"},{\"n\":\"推荐\",\"v\":\"commend\"}]}],\"qita\":[{\"key\":0,\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"纪录片\",\"v\":\"Jlp\"},{\"n\":\"经典片\",\"v\":\"Jdp\"},{\"n\":\"经典剧\",\"v\":\"Jdj\"},{\"n\":\"网大电影\",\"v\":\"wlp\"},{\"n\":\"国产老电影\",\"v\":\"laodianying\"}]},{\"key\":11,\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"},{\"n\":\"more\",\"v\":\"more\"}]},{\"key\":3,\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"Netflix\",\"v\":\"Netflix\"},{\"n\":\"惊悚\",\"v\":\"惊悚\"},{\"n\":\"悬疑\",\"v\":\"悬疑\"},{\"n\":\"Top250\",\"v\":\"Top250\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"漫改\",\"v\":\"漫改\"},{\"n\":\"灾难\",\"v\":\"灾难\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"文艺\",\"v\":\"文艺\"},{\"n\":\"西部\",\"v\":\"西部\"},{\"n\":\"丧尸\",\"v\":\"丧尸\"},{\"n\":\"传记\",\"v\":\"传记\"},{\"n\":\"经典\",\"v\":\"经典\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"音乐\",\"v\":\"音乐\"},{\"n\":\"穿越\",\"v\":\"穿越\"},{\"n\":\"武侠\",\"v\":\"武侠\"},{\"n\":\"歌舞\",\"v\":\"歌舞\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"名著\",\"v\":\"名著\"},{\"n\":\"同性\",\"v\":\"同性\"},{\"n\":\"运动\",\"v\":\"运动\"},{\"n\":\"邵氏\",\"v\":\"邵氏\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"短片\",\"v\":\"短片\"},{\"n\":\"儿童\",\"v\":\"儿童\"},{\"n\":\"恐怖\",\"v\":\"恐怖\"},{\"n\":\"二次元\",\"v\":\"二次元\"},{\"n\":\"商战\",\"v\":\"商战\"}]},{\"key\":1\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"马来西亚\",\"v\":\"马来西亚\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":2,\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hit\"},{\"n\":\"推荐\",\"v\":\"commend\"}]}]}");
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }

    /**
     * 爬虫headers
     *
     * @param url
     * @return
     */
    protected HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("method", "GET");
        headers.put("Host", siteHost);
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("DNT", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        return headers;
    }

    /**
     * 获取分类数据 + 首页最近更新视频列表数据
     *
     * @param filter 是否开启筛选 关联的是 软件设置中 首页数据源里的筛选开关
     * @return
     */
    @Override
    public String homeContent(boolean filter) {
        try {
            Document doc = Jsoup.parse(OkHttpUtil.string(siteUrl, getHeaders(siteUrl)));
            // 分类节点
            Elements elements = doc.select("ul[class='navbar-nav mr-auto']> li a");
            JSONArray classes = new JSONArray();
            for (Element ele : elements) {
                //分类名
                String name = ele.text();
                boolean show = name.equals("电影") ||
                        name.equals("电视剧") ||
                        name.equals("综艺") ||
                        name.equals("动漫") ||
                        name.equals("其他");
                if (show) {
                    Matcher mather = regexCategory.matcher(ele.attr("href"));
                    if (!mather.find())
                        continue;
                    // 把分类的id和名称取出来加到列表里
                    String id = mather.group(1).trim();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type_id", id);
                    jsonObject.put("type_name", name);
                    classes.put(jsonObject);
                }
            }
            JSONObject result = new JSONObject();
            if (filter) {
                result.put("filters", filterConfig);
            }
            result.put("class", classes);
            try {
                // 取首页推荐视频列表
                Element homeList = doc.select("ul.threadlist").get(0);
                Elements list = homeList.select("li");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst("h2 a").attr("title");
                    String cover = vod.selectFirst("a img").attr("src");
                    String remark = vod.selectFirst("a button").text();
                    Matcher matcher = regexVid.matcher(vod.selectFirst("a").attr("href"));
                    if (!matcher.find())
                        continue;
                    String id = matcher.group(1);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", remark);
                    videos.put(v);
                }
                result.put("list", videos);
            } catch (Exception e) {
                SpiderDebug.log(e);
            }
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    /**
     * 获取分类信息数据
     *
     * @param tid    分类id
     * @param pg     页数
     * @param filter 同homeContent方法中的filter
     * @param extend 筛选参数{k:v, k1:v1}
     * @return
     */
    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        // 获取分类数据的url
        String url = "";
        try {
            if (extend != null && extend.size() > 0) {
                for (Iterator<String> it = extend.keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    String value = extend.get(key);
                    if (value != null && value.length() != 0 && value != " ") {
                        url = siteUrl + "/" + tid + "/" + value;
                    } else {
                        url = siteUrl + "/" + tid;
                    }
                    ;
                }
            } else {
                url = siteUrl + "/" + tid;
            }
            ;
            if (pg.equals("1")) {
                url = url + "/index.html";
            } else {
                url = url + "/index" + pg + ".html";
            }
            //System.out.println(url);
            String html = OkHttpUtil.string(url, getHeaders(url));
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            int pageCount = 0;
            int page = -1;

            // 取页码相关信息
            Elements pageInfo = doc.select("ul.pagination li");
            if (pageInfo.size() == 0) {
                page = Integer.parseInt(pg);
                pageCount = page;
            } else {
                for (int i = 0; i < pageInfo.size(); i++) {
                    Element li = pageInfo.get(i);
                    Element a = li.selectFirst("a");
                    if (a == null)
                        continue;
                    String name = a.text();
                    //System.out.println("名称"+name);
                    if (page == -1 && li.hasClass("active")) {
                        Matcher matcher = regexPage.matcher(a.attr("href"));
                        if (matcher.find()) {
                            //System.out.println("哈哈"+matcher.group(1));
                            page = Integer.parseInt(matcher.group(1));
                        } else {
                            Elements list = doc.select("ul.threadlist li");
                            if (list.size() > 0) {
                                page = 1;
                            } else {
                                page = 0;
                            }
                        }
                    }
                    if (name.equals("尾页")) {
                        Matcher matcher = regexPage.matcher(a.attr("href"));
                        if (matcher.find()) {
                            //System.out.println("尾页" + matcher.group(1));
                            pageCount = Integer.parseInt(matcher.group(1));
                        } else {
                            pageCount = 0;
                        }
                        break;
                    }
                }
            }

            JSONArray videos = new JSONArray();
            if (!html.contains("没有找到您想要的结果哦")) {
                // 取当前分类页的视频列表
                Elements list = doc.select("ul.threadlist li");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst("h2 a").attr("title");
                    String cover = vod.selectFirst("a img").attr("src");
                    String remark = vod.selectFirst("a button").text();
                    Matcher matcher = regexVid.matcher(vod.selectFirst("a").attr("href"));
                    if (!matcher.find())
                        continue;
                    String id = matcher.group(1);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", remark);
                    videos.put(v);
                }
            }
            result.put("page", page);
            result.put("pagecount", pageCount);
            result.put("limit", 20);
            result.put("total", pageCount <= 1 ? videos.length() : pageCount * 20);

            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    /**
     * 视频详情信息
     *
     * @param ids 视频id
     * @return
     */
    @Override
    public String detailContent(List<String> ids) {
        try {
            // 视频详情url
            String url = siteUrl + "/" + ids.get(0) + "/";
            //System.out.println(url);
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();

            // 取基本数据
            String cover = doc.selectFirst("div.cover a").attr("href");
            String title = doc.selectFirst("div.cover a").attr("title");
            Elements list1 = doc.select("div.message>p");
            String desc = doc.select("div.message>p").get(list1.size() - 1).text();
            String category = "", area = "", year = "", remark = "", director = "", actor = "";
            Elements span_text_muted = doc.select("div.message>p");
            for (int i = 0; i < span_text_muted.size() - 2; i++) {
                Element text = span_text_muted.get(i);
                String info = text.text();
                if (info.contains("分类")) {
                    try {
                        category = text.text().split(": ")[1];
                    } catch (Exception e) {
                        category = "";
                    }
                } else if (info.contains("年份")) {
                    try {
                        year = text.text().split(": ")[1].split("-")[0];
                    } catch (Exception e) {
                        year = "";
                    }
                } else if (info.contains("地区")) {
                    try {
                        area = text.text().split(": ")[1];
                    } catch (Exception e) {
                        area = "";
                    }
                } else if (info.contains("影片备注")) {
                    try {
                        remark = text.text().split(": ")[1];
                    } catch (Exception e) {
                        remark = "";
                    }
                } else if (info.contains("导演")) {
                    try {
                        director = text.text().split(": ")[1];
                    } catch (Exception e) {
                        director = "";
                    }
                } else if (info.contains("主演")) {
                    try {
                        actor = text.text().split(": ")[1];
                    } catch (Exception e) {
                        actor = "";
                    }
                }
            }

            vodList.put("vod_id", ids.get(0));
            vodList.put("vod_name", title);
            vodList.put("vod_pic", cover);
            vodList.put("type_name", category);
            vodList.put("vod_year", year);
            vodList.put("vod_area", area);
            vodList.put("vod_remarks", remark);
            vodList.put("vod_actor", actor);
            vodList.put("vod_director", director);
            vodList.put("vod_content", desc);
            //System.out.println(vodList.toString());
            Map<String, String> vod_play = new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    try {
                        int sort1 = playerConfig.getJSONObject(o1).getInt("or");
                        int sort2 = playerConfig.getJSONObject(o2).getInt("or");

                        if (sort1 == sort2) {
                            return 1;
                        }
                        return sort1 - sort2 > 0 ? 1 : -1;
                    } catch (JSONException e) {
                        SpiderDebug.log(e);
                    }
                    return 1;
                }
            });

            // 取播放列表数据
            Elements sources = doc.select("div#player_list>h2");
            //System.out.println(sources.size());
            Elements sourceList = doc.select("div#player_list>ul");
            //System.out.println(sourceList.size());
            for (int i = 0; i < sources.size(); i++) {
                Element source = sources.get(i);
                //System.out.println(sources.text().split("：")[0].split("』")[1]);
                String sourceName = source.text().split("』")[1].split("：")[0];
                boolean found = false;
                for (Iterator<String> it = playerConfig.keys(); it.hasNext(); ) {
                    String flag = it.next();
                    if (playerConfig.getJSONObject(flag).getString("sh").equals(sourceName)) {
                        sourceName = flag;
                        found = true;
                        break;
                    }
                }
                if (!found)
                    continue;
                String playList = "";
                Elements playListA = sourceList.get(i).select("li > a");
                //System.out.println(playListA.size());
                List<String> vodItems = new ArrayList<>();

                for (int j = 0; j < playListA.size(); j++) {
                    Element vod = playListA.get(j);
                    Matcher matcher = regexPlay.matcher(vod.attr("href"));
                    if (!matcher.find())
                        continue;
                    String playURL = matcher.group(1);
                    vodItems.add(vod.text() + "$" + playURL);
                }
                if (vodItems.size() > 0)
                    playList = TextUtils.join("#", vodItems);

                if (playList.length() == 0)
                    continue;

                vod_play.put(sourceName, playList);
            }

            if (vod_play.size() > 0) {
                String vod_play_from = TextUtils.join("$$$", vod_play.keySet());
                String vod_play_url = TextUtils.join("$$$", vod_play.values());
                vodList.put("vod_play_from", vod_play_from);
                vodList.put("vod_play_url", vod_play_url);
            }
            JSONArray list = new JSONArray();
            list.put(vodList);
            result.put("list", list);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    /**
     * 获取视频播放信息
     *
     * @param flag     播放源
     * @param id       视频id
     * @param vipFlags 所有可能需要vip解析的源
     * @return
     */
    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            // 播放页 url
            String url = siteUrl + id;
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            /*取得script下面的JS变量*/
            Elements e = doc.select("div>script");
            /*循环遍历script下面的JS变量*/
            String player = "";
            String pn = "";
            JSONObject result = new JSONObject();
            for (Element element : e) {
                /*取得JS变量数组*/
                String[] data = element.data().toString().split("var");
                /*取得单个JS变量*/
                for (String variable : data) {
                    /*过滤variable为空的数据*/
                    if (variable.contains("=")) {
                        /*取到满足条件的JS变量*/
                        if (variable.contains("now")) {
                            String[] kvp = variable.split("=");
                            player = kvp[1].replaceAll("\"", "").replaceAll(";", "");
                            if (player.startsWith("base64")) {
                                String[] plist1 = player.split("\\(");
                                String[] plist2 = plist1[1].split("\\)");
                                // 解码
                                player = new String(Base64.decode(plist2[0].getBytes(), Base64.DEFAULT));
                                //byte[] base64Data = Base64.getDecoder().decode(plist2[0]);
                                //player = new String(base64Data, "utf-8");
                            }

                            if (!player.startsWith("http")) {
                                player = siteUrl + player;
                            }
                        }
                        if (variable.contains("pn")) {
                            String[] kvp = variable.split("=");
                            pn = kvp[1].replaceAll("\"", "").replaceAll(";", "");
                        }
                    }
                }
                if (playerConfig.has(pn)) {
                    JSONObject pCfg = playerConfig.getJSONObject(pn);
                    String videoUrl = player;
                    String playUrl = pCfg.getString("pu");
                    result.put("parse", pCfg.getInt("sn"));
                    result.put("playUrl", playUrl);
                    result.put("url", videoUrl);
                    result.put("header", "");
                }
            }
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    /**
     * 搜索
     *
     * @param key
     * @param quick 是否播放页的快捷搜索
     * @return
     */
    @Override
    public String searchContent(String key, boolean quick) {
        try {
            String url = siteUrl + "/search.php?searchword=" + URLEncoder.encode(key);
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();

            JSONArray videos = new JSONArray();
            Elements list = doc.select("div.card-body>ul>li.media");
            for (int i = 0; i < list.size(); i++) {
                Element vod = list.get(i);
                String title = vod.select("div.media-body>div.subject>a>span").text();
                String cover = "";
                String remark = vod.selectFirst("div.media-body>div.d-flex>div.text-muted>span").text();
                Matcher matcher = regexVid.matcher(vod.select("div.media-body>div.subject>a").attr("href"));
                if (!matcher.find())
                    continue;
                String id = matcher.group(1);
                JSONObject v = new JSONObject();

                // 视频封面
                String vodurl = siteUrl + "/" + id + "/";
                Document voddoc = Jsoup.parse(OkHttpUtil.string(vodurl, getHeaders(vodurl)));
                cover = voddoc.selectFirst("div.cover a").attr("href");

                v.put("vod_id", id);
                v.put("vod_name", title);
                v.put("vod_pic", cover);
                v.put("vod_remarks", remark);
                videos.put(v);
            }

            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }
}
