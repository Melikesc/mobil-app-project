package com.example.notlar;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SQLiteManager extends SQLiteOpenHelper {

    public static SQLiteManager sqLiteManager;

    public static final String DATABASE_NAME = "NoteDB";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "Note";
    public static final String COUNTER = "counter";

    public static final String ID_FIELD = "id";
    public static final String TITLE_FIELD = "title";
    public static final String DESC_FIELD = "desc";
    public static final String DELETED_FIELD = "deleted";

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    public SQLiteManager( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteManager instanceOfDatabase(Context context){
        if(sqLiteManager == null){
            sqLiteManager= new SQLiteManager(context);
        }
        return sqLiteManager;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) { //eğer bir tablo yoksa tablo oluşturur.
        StringBuilder sql;
        sql = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append("(")
                .append(COUNTER)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(ID_FIELD)
                .append(" INT, ")
                .append(TITLE_FIELD)
                .append(" TEXT, ")
                .append(DESC_FIELD)
                .append(" TEXT, ")
                .append(DELETED_FIELD)
                .append(" TEXT)");

        sqLiteDatabase.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public void addNoteToDatabase(Note note){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, note.getId());
        contentValues.put(TITLE_FIELD, note.getTitle());
        contentValues.put(DESC_FIELD, note.getDescription());
        contentValues.put(DELETED_FIELD, getStringFromDate(note.getDeleted()));

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues); //Note tabloasuna contenvalues icindeki degerleri ekle.
        //INSERT INTO Note (id,title,desc,deleted) values (1,'Alinacaklar listesi','elma domates sogan','2022-05-29 14:26:05')
        //INSERT INTO table_name (column1, column2, column3, ...) VALUES (value1, value2, value3, ...);
    }


    public void populateNoteListArray(){//Veritabanındaki bütün notları çekip noteArrayListesi içerisine atıyor.
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try(Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM "+ TABLE_NAME, null))
        {
            if(result.getCount() != 0)
            {
                while (result.moveToNext())
                { // while result.moveToNext() null gelene kadar devam edecek
                    int id = result.getInt(1);
                    String title =result.getString(2);
                    String desc =result.getString(3);
                    String stringDeleted =result.getString(4);
                    Date deleted = getDateFromString(stringDeleted);
                    Note note = new Note(id, title,desc,deleted);
                    Note.noteArrayList.add(note);

                }
            }
        }
    }

    public void updateNoteInDB(Note note){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, note.getId());
        contentValues.put(TITLE_FIELD, note.getTitle());
        contentValues.put(DESC_FIELD, note.getDescription());
        contentValues.put(DELETED_FIELD, getStringFromDate(note.getDeleted()));

        sqLiteDatabase.update(TABLE_NAME, contentValues, ID_FIELD + "=?", new String[]{String.valueOf(note.getId())});
        //UPDATE Note SET id = 1 , title = 'Alinacaklar listesi', desc = 'elma sogan havuc', deleted = '2022-05-29 14:26:05' where id ='1'
        //UPDATE table_name
        //SET column1 = value1, column2 = value2, ...
        //WHERE condition;
    }

    private String getStringFromDate(Date date) {
        if(date == null)    //2022-05-29 14:26:05
            return null;
        return dateFormat.format(date);
    }

    private Date getDateFromString(String string){ //'2022-05-29 14:26:05' class String
        try
        { //Hata gelirse program catch'e girsin
            return dateFormat.parse(string);  // 2022-05-29 14:26:05 class DateFormat
        }
        catch (ParseException | NullPointerException e) //Hata string degeri bos ise null gondersin
        {
            return  null;
        }
    }
}
