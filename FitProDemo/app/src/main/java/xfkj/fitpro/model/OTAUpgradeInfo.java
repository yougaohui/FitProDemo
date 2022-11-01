package xfkj.fitpro.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaohui.you on 2020/9/1 0001
 * Email:839939978@qq.com
 */
public class OTAUpgradeInfo {

    /**
     * success : true
     * data : {"id":54,"name":"YOHO2","version":"1.0.0","content":"优化用户体验","status":1,"type":0,"force":0,"display_name":"YOHO2固件","bundle_identifier":"com.yoho2","version_code":1,"app_down_url":"http://static.gulaike.com/yoho2/yoho_bin.zip","api_url":"","faq_url":null,"created_at":"2020-09-01 15:13:05","updated_at":"2020-09-01 11:22:07"}
     * error : null
     */

    private boolean success;
    private DataBean data;
    private String error;

    public static OTAUpgradeInfo objectFromData(String str) {

        return new Gson().fromJson(str, OTAUpgradeInfo.class);
    }

    public static OTAUpgradeInfo objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), OTAUpgradeInfo.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<OTAUpgradeInfo> arrayOTAUpgradeInfoFromData(String str) {

        Type listType = new TypeToken<ArrayList<OTAUpgradeInfo>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<OTAUpgradeInfo> arrayOTAUpgradeInfoFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<OTAUpgradeInfo>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static class DataBean {
        /**
         * id : 54
         * name : YOHO2
         * version : 1.0.0
         * content : 优化用户体验
         * status : 1
         * type : 0
         * force : 0
         * display_name : YOHO2固件
         * bundle_identifier : com.yoho2
         * version_code : 1
         * app_down_url : http://static.gulaike.com/yoho2/yoho_bin.zip
         * api_url :
         * faq_url : null
         * created_at : 2020-09-01 15:13:05
         * updated_at : 2020-09-01 11:22:07
         */

        private int id;
        private String name;
        private String version;
        private String content;
        private int status;
        private int type;
        private int force;
        private String display_name;
        private String bundle_identifier;
        private int version_code;
        private String app_down_url;
        private String api_url;
        private Object faq_url;
        private String created_at;
        private String updated_at;

        public static DataBean objectFromData(String str) {

            return new Gson().fromJson(str, DataBean.class);
        }

        public static DataBean objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new Gson().fromJson(jsonObject.getString(str), DataBean.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static List<DataBean> arrayDataBeanFromData(String str) {

            Type listType = new TypeToken<ArrayList<DataBean>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public static List<DataBean> arrayDataBeanFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new TypeToken<ArrayList<DataBean>>() {
                }.getType();

                return new Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getForce() {
            return force;
        }

        public void setForce(int force) {
            this.force = force;
        }

        public String getDisplay_name() {
            return display_name;
        }

        public void setDisplay_name(String display_name) {
            this.display_name = display_name;
        }

        public String getBundle_identifier() {
            return bundle_identifier;
        }

        public void setBundle_identifier(String bundle_identifier) {
            this.bundle_identifier = bundle_identifier;
        }

        public int getVersion_code() {
            return version_code;
        }

        public void setVersion_code(int version_code) {
            this.version_code = version_code;
        }

        public String getApp_down_url() {
            return app_down_url;
        }

        public void setApp_down_url(String app_down_url) {
            this.app_down_url = app_down_url;
        }

        public String getApi_url() {
            return api_url;
        }

        public void setApi_url(String api_url) {
            this.api_url = api_url;
        }

        public Object getFaq_url() {
            return faq_url;
        }

        public void setFaq_url(Object faq_url) {
            this.faq_url = faq_url;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
    }
}
