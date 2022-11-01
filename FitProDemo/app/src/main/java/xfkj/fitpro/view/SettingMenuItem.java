package xfkj.fitpro.view;

;import java.io.Serializable;

/**
 * Created by PQQ on 2016-9-29.
 */

public class SettingMenuItem implements Serializable
{

    /**
     * 菜单名称
     */
    public int Id;

    public String Name = "";

    public String NameInfo = "";


    /**
     * 图标资源
     */
    public int Resource;

    /**
     * 图标资源
     */
    public int BgResource;

    /**
     * 0 父类 1 子类 2 空格
     */
    public int MenuType = 1;

    /**
     * 跳转的页面
     */
    public Class<?> ClassObj;

    public boolean isHasDivision = false;


    public Object Tag = null;

    public SettingMenuItem()
    {
        super();
    }

    public SettingMenuItem(int id,String name, int resource, int menuType, Class<?> cla)
    {
        this();
        Id = id;
        Name = name;
        Resource = resource;
        MenuType = menuType;
        this.ClassObj = cla;
    }

    public SettingMenuItem(int id,String name, String info, int resource, int menuType, Class<?> cla)
    {
        this();
        Id = id;
        Name = name;
        Resource = resource;
        MenuType = menuType;
        NameInfo = info;
        this.ClassObj = cla;
    }


    public SettingMenuItem(int id,String name, int resource, int bgresource, int menuType, Class<?> cla)
    {
        this();
        Id = id;
        Name = name;
        Resource = resource;
        MenuType = menuType;
        this.ClassObj = cla;
        this.BgResource = bgresource;
    }

    public SettingMenuItem(int id,String name, String nameinfo, int resource, int bgresource, int menuType, boolean isHasDivision, Class<?> cla)
    {
        this();
        Id = id;
        Name = name;
        Resource = resource;
        MenuType = menuType;
        this.ClassObj = cla;
        this.BgResource = bgresource;
        this.NameInfo = nameinfo;
        this.isHasDivision = isHasDivision;
    }


    public SettingMenuItem(int id,String name, int resource, int bgresource, boolean isHasDivision, int menuType, Class<?> cla)
    {
        this();
        Id = id;
        Name = name;
        Resource = resource;
        MenuType = menuType;
        this.ClassObj = cla;
        this.BgResource = bgresource;
        this.isHasDivision = isHasDivision;
    }

    public SettingMenuItem(int id,String name, String nameinfo, int resource, int bgresource, boolean isHasDivision, int menuType, Class<?> cla)
    {
        this();
        Id = id;
        Name = name;
        Resource = resource;
        MenuType = menuType;
        this.ClassObj = cla;
        this.BgResource = bgresource;
        this.NameInfo = nameinfo;
        this.isHasDivision = isHasDivision;
    }


    public String getNameInfo()
    {
        return NameInfo;
    }

    public void setNameInfo(String nameInfo)
    {
        NameInfo = nameInfo;
    }

    public int getResource()
    {
        return Resource;
    }

    public void setResource(int resource)
    {
        Resource = resource;
    }

    public boolean isHasDivision()
    {
        return isHasDivision;
    }

    public void setHasDivision(boolean hasDivision)
    {
        isHasDivision = hasDivision;
    }
}
