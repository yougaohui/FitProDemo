package xfkj.fitpro.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;

/**
 * 数据库操作类
 *
 * @author pqq
 */
public class SqliteDBAcces
{
    private SQLiteDatabase mDatabase;
    public String FilePath;
    public String FileName;
	private int dbVersion = 1;

    public SqliteDBAcces(SQLiteDatabase database)
    {
        this.mDatabase = database;
        createTable();
    }

    public SQLiteDatabase getDatabase()
    {
        return mDatabase;
    }

	
	 /**
     * 建立数据表
     * */
    private void createTable(){
		//步数  系统时间 时间 分钟  步数   卡里路 行走距离 备注
        mDatabase.execSQL("create table if not exists Step (ID INTEGER PRIMARY KEY AUTOINCREMENT,SportDate varchar(100),LongDate LONG,ActiveTime INTEGER,Calory INTEGER,Distance INTEGER,Mode INTEGER,Offset INTEGER,Steps INTEGER)");
		
		//睡眠  系统时间 接收时间 类型  数据  备注
        mDatabase.execSQL("create table if not exists Sleep (ID INTEGER PRIMARY KEY AUTOINCREMENT,RevDate varchar(100),LongDate LONG,Data INTEGER,Offset INTEGER,SleepTypes INTEGER)");
		
		
		//测量  系统时间 测试时间 心率 高血压 低血压 血氧
        mDatabase.execSQL("create table if not exists Measure (ID INTEGER PRIMARY KEY AUTOINCREMENT,SysDate varchar(100),LongDate LONG,RevDate varchar(100),Heart varchar(100),hBlood varchar(100),lBlood varchar(100),Spo varchar(100))");
		
		
        int nowVersion  = mDatabase.getVersion();
        mDatabase.setVersion(dbVersion);
        if(nowVersion != dbVersion){
        //    mDatabase.execSQL("Alter table Pwds add column level varchar(16)");
	    }
    }

    /**
     * 查询语句
     *
     * @param cmdText
     * @return
     */
    public Cursor Query(String cmdText)
    {
        if (mDatabase == null || mDatabase.isOpen() == false)
            return null;
        try
        {
            return mDatabase.rawQuery(cmdText, null);
        } catch (SQLiteException e)
        {
            Logdebug("SQLITE ERROR", e.getMessage());
        }
        return null;
    }

	//删除数据dao.Delete("table","=?&=?",new String[]{"",""});
    public boolean Delete(String table, String whereClause, String[] whereArgs)
    {
        int d = mDatabase.delete(table, whereClause, whereArgs);
        if (d > 0)
        {
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * Sql语句
     *
     * @param cmdText
     * @return
     */
    public boolean Execute(String cmdText)
    {
        try
        {
            mDatabase.execSQL(cmdText);
        } catch (SQLException e)
        {
            Logdebug("SQLITE ERROR", e.getMessage());
            return false;
        }
        return true;
    }

    public void getClose()
    {
        if (mDatabase == null || mDatabase.isOpen() == false)
            return;
        mDatabase.close();
    }

    /**
     * 删除表
     *
     * @param tableName
     * @return
     */
    public boolean DropTable(String tableName)
    {
        // 取得所有表
        String strSQLText = String.format("DROP TABLE %s ", tableName);
        return Execute(strSQLText);
    }
}
