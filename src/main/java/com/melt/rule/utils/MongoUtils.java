package com.melt.rule.utils;

import com.melt.rule.bean.RuleConstant;
import com.melt.rule.bean.ThreadLocalHolder;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.InputStream;
import java.util.*;

import static com.mongodb.client.model.Projections.*;

/**
 * mongod 操作工具类
 * author: melt
 * date: 2017-11-09
 */
public class MongoUtils {

//	private static MongoClient mongoClient;
//	private static GridFSBucket gridFSBucket ;

    /**
     * 检查不能为空
     * @param dbName    数据库名称
     * @param colName   集合名称
     */
    public static void checkNotNull(String dbName,String colName){
        Assert.notNull(dbName,"数据库名称不能为空！");
        Assert.notNull(colName,"集合名称不能为空！");
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
        Assert.notNull(mongoClient,"mongoClient不能为空，请查询配置！");
    }
	/**
	 * 查找对象 - 根据主键_id
     * @param dbName    数据库名称
     * @param colName   集合名称
	 * @param id        主键
	 * @return
	 */
	public static Document queyrById(String dbName,String colName, String id) {
        checkNotNull(dbName,colName);
        Assert.notNull(id,"查询主键不能为空！");
        ObjectId _idobj = new ObjectId(id);;
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
		return mongoClient.getDatabase(dbName).getCollection(colName).find(Filters.eq("_id", _idobj)).first() ;
	}


	
	/**
	 * 记录行数
     * @param dbName    数据库名称
     * @param colName   集合名称
	 * @param filter    条件
	 * @return
	 */
	public static Long getCount(String dbName,String colName, Bson filter) {
        checkNotNull(dbName,colName);
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(colName);
        return filter != null ? collection.count(filter) : collection.count();
	}


    /**
     * 条件查询
     * @param dbName    数据库名称
     * @param colName   集合名称
     * @param filter    条件
     * @return
     */
	public static List<Document> query(String dbName, String colName, Bson filter) {
        checkNotNull(dbName,colName);
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
		return mongoClient.getDatabase(dbName).getCollection(colName).find(filter).into(new ArrayList<Document>()) ;
	}

    /**
     * 条件查询(分页)
     * @param dbName    数据库名称
     * @param colName   集合名称
     * @param filter    条件
     * @param pageNo    页码
     * @param pageSize  页大小
     * @return
     */
	public static List<Document> queryByPage(String dbName, String colName, Bson filter, int pageNo, int pageSize) {
        checkNotNull(dbName,colName);
		Bson orderBy = new BasicDBObject("_id", 1);
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
		return mongoClient.getDatabase(dbName).getCollection(colName).find(filter)
                .sort(orderBy).skip((pageNo - 1) * pageSize).limit(pageSize).into(new ArrayList<>()) ;
	}

    /**
     * 条件查询(分页)
     * @param dbName    数据库名称
     * @param colName   集合名称
     * @param cols      显示列
     * @param filter    条件
     * @param orderBy   排序
     * @param pageNo    页码
     * @param pageSize  页大小
     * @return
     */
    public static List<Document> queryByPage(String dbName,String colName,List<String> cols,Bson filter,
                                              Bson orderBy,int pageNo, int pageSize) {
        checkNotNull(dbName,colName);
        FindIterable<Document> fi = null ;
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
        if(filter != null){
            fi = mongoClient.getDatabase(dbName).getCollection(colName).find(filter) ;
        } else {
            fi = mongoClient.getDatabase(dbName).getCollection(colName).find();
        }
        /***
         * 查询指定列
         */
        if(cols != null && !cols.isEmpty()){
            if(cols.contains("_id")){
                fi = fi.projection(fields(include(cols), excludeId())) ;
            } else {
                fi = fi.projection(Projections.include(cols)) ;
            }
        }

        if(orderBy != null){
            fi = fi.sort(orderBy);
        }
        if(pageNo > 0 && pageSize > 0){
            fi = fi.skip((pageNo - 1) * pageSize).limit(pageSize);
        }
        return fi.into(new ArrayList<>()) ;
    }

    /**
     * 条件查询(分页)
     * @param dbName    数据库名称
     * @param colName   集合名称
     * @param hideCols  显示列
     * @param filter    条件
     * @param orderBy   排序
     * @param pageNo    页码
     * @param pageSize  页大小
     * @return  List<Document>
     */
    public static List<Document> queryByPage2(String dbName,String colName,List<String> hideCols,Bson filter,
                                                     Bson orderBy,int pageNo, int pageSize) {
        checkNotNull(dbName,colName);
        FindIterable<Document> fi = null ;
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
        if(filter != null){
            fi = mongoClient.getDatabase(dbName).getCollection(colName).find(filter) ;
        } else {
            fi = mongoClient.getDatabase(dbName).getCollection(colName).find();
        }
        /***
         * 剔除不显示的列
         */
        if(hideCols != null && !hideCols.isEmpty()){
            fi = fi.projection(Projections.exclude(hideCols)) ;
        }

        if(orderBy != null){
            fi = fi.sort(orderBy);
        }
        if(pageNo > 0 && pageSize > 0){
            fi = fi.skip((pageNo - 1) * pageSize).limit(pageSize);
        }
        return fi.into(new ArrayList<>()) ;
    }

	/**
	 * 通过ID列表查询 
     * @param dbName    数据库名称
     * @param colName   集合名称
	 * @param ids       主键列表
	 * @return
	 */
	public static List<Document> queryByIds(String dbName,String colName,List<String> cols,List<String> ids){
        checkNotNull(dbName,colName);
		if(CollectionUtils.isEmpty(ids)){
			return null ;
		}
		
		List<ObjectId> idsList = new ArrayList<ObjectId>() ;
		for(String id : ids){
			idsList.add(new ObjectId(id)) ;
		}
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
		FindIterable<Document> fi = mongoClient.getDatabase(dbName).getCollection(colName)
                            .find(Filters.in("_id", idsList)) ;
		/***
		 * 查询指定列
		 */
		if(cols != null && !cols.isEmpty()){
			if(!cols.contains("_id")){
				fi = fi.projection(fields(include(cols), excludeId())) ;
			} else {
				fi = fi.projection(Projections.include(cols)) ;
			}
		}
		return fi.into(new ArrayList<Document>()) ;
	}


    /**
     *  查询
     * @param dbName    数据库名称
     * @param colName   集合名称
     * @param filter
     * @param orderBy
     * @return
     */
	public static List<Document> query(String dbName,String colName,Bson filter,Bson orderBy) {
        checkNotNull(dbName,colName);
		return queryByPage(dbName,colName,null, filter, orderBy, 0, 0);
	}

    /**
     * 插入对象(默认会加上last_time日期时间)
     * @param dbName    数据库名称
     * @param colName   集合名称
     * @param doc       集合对象
     * @return
     */
	public static String insertOne(String dbName,String colName,Document doc){
		if(!doc.containsKey("last_time")){
			doc.append("last_time", new Date());
		}
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
        mongoClient.getDatabase(dbName).getCollection(colName).insertOne(doc);
		return doc.getObjectId("_id").toString() ;
	}


    /**
     * 通过ID主键删除
     * @param dbName    数据库名称
     * @param colName   集合名称
     * @param id        主键
     * @return
     */
	public static int deleteById(String dbName,String colName, String id) {
        checkNotNull(dbName,colName);
		int count = 0;
		ObjectId _id =  new ObjectId(id);

		Bson filter = Filters.eq("_id", _id);
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
		DeleteResult deleteResult = mongoClient.getDatabase(dbName).getCollection(colName).deleteOne(filter);
		count = (int) deleteResult.getDeletedCount();
		return count;
	}

    /**
     * 按条件删除
     * @param dbName    数据库名称
     * @param colName   集合名称
     * @param filter    条件
     * @return
     */
	public static int deleteByWhere(String dbName,String colName, Bson filter) {
        checkNotNull(dbName,colName);
		int count = 0;
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
		DeleteResult deleteResult = mongoClient.getDatabase(dbName).getCollection(colName).deleteMany(filter);
		count = (int) deleteResult.getDeletedCount();
		return count;
	}

    /**
     * 修改对象
     * @param dbName    数据库名称
     * @param colName   集合名称
     * @param id        主键
     * @param newdoc    需要修改值
     * @return
     */
	public static int updateById(String dbName,String colName, String id, Document newdoc) {
        checkNotNull(dbName,colName);
		if(!newdoc.containsKey("last_time")){
			newdoc.append("last_time", new Date());
		}
		ObjectId _idobj = new ObjectId(id);;
		Bson filter = Filters.eq("_id", _idobj);
		// coll.replaceOne(filter, newdoc); // 完全替代
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
        mongoClient.getDatabase(dbName).getCollection(colName).updateOne(filter, new Document("$set", newdoc));
		
		return 1;
	}

    /**
     *  属性值 加1
     * @param dbName    数据库名称
     * @param colName   集合名称
     * @param id        主键
     * @param col       属性
     * @return
     */
	public static int incById(String dbName,String colName, String id, String col) {
        checkNotNull(dbName,colName);

		Bson filter = Filters.eq("_id", new ObjectId(id));
		// coll.replaceOne(filter, newdoc); // 完全替代
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
        mongoClient.getDatabase(dbName).getCollection(colName).
                updateOne(filter, new Document("$inc", new Document(col,1)));
		
		return 1;
	}

    /**
     * 属性值 减1
     * @param dbName    数据库名称
     * @param colName   集合名称
     * @param id        主键
     * @param col       属性
     * @return
     */
	public static int desById(String dbName,String colName, String id, String col) {
        checkNotNull(dbName,colName);
		Bson filter = Filters.eq("_id", new ObjectId(id));
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
        mongoClient.getDatabase(dbName).getCollection(colName).
                updateOne(filter, new Document("$inc", new Document(col,-1)));
		
		return 1;
	}

	/**
	 * 上传文件
     * @param dbName		数据库名称
	 * @param fileName		文件名
	 * @param inputStream	输入流
	 * @param metadata		字典
	 * @return  流ID
	 */
	public static String uploadFromStream(String dbName,String fileName,
                                          InputStream inputStream,Document metadata){
        Assert.notNull(dbName,"数据库名称不能为空！");
        Assert.notNull(fileName,"集合名称不能为空！");
        Assert.notNull(inputStream,"文件流不能为空！");
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
        Assert.notNull(mongoClient,"mongoClient不能为空，请查询配置！");
        MongoDatabase database = mongoClient.getDatabase(dbName);
		GridFSUploadOptions options = new GridFSUploadOptions()
        	.metadata(metadata);
		ObjectId fileId = GridFSBuckets.create(database).uploadFromStream(fileName, inputStream, options);
		return fileId.toString();
	}

    /**
     * 下载文件（这种方式存在文件下不全问题，现在没有细查原因）
     * @param dbName	数据库名称
     * @param id        主键
     * @return
     */
	public static byte[] downloadStream(String dbName,String id){
        Assert.notNull(dbName,"数据库名称不能为空！");
        Assert.notNull(id,"主键id不能为空！");
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
        Assert.notNull(mongoClient,"mongoClient不能为空，请查询配置！");

        MongoDatabase database = mongoClient.getDatabase(dbName);
		GridFSDownloadStream downloadStream = GridFSBuckets.create(database).openDownloadStream(new ObjectId(id));
        int fileLength = (int) downloadStream.getGridFSFile().getLength();
        byte[] bytesToWriteTo = new byte[fileLength];
        downloadStream.read(bytesToWriteTo);
        downloadStream.close();
        return bytesToWriteTo ;
	}
	
//	/**
//	 * 下载文件 （解决文件下载不全问题）
//	 * @param id
//	 * @return
//	 */
//	public static void downloadStream(String id,HttpServletResponse response){
//		getDB();
//		ObjectId _idobj = null;
//		try {
//			_idobj = new ObjectId(id);
//		} catch (Exception e) {
//		}
//
//		try {
//			gridFSBucket.downloadToStream(_idobj, response.getOutputStream());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 文件信息
     * @param dbName	数据库名称
     * @param id        主键
	 * @return
	 */
	public static Map<String,String> getGridFileInfo(String dbName,String id){
        Assert.notNull(dbName,"数据库名称不能为空！");
        Assert.notNull(id,"主键id不能为空！");
        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
        Assert.notNull(mongoClient,"mongoClient不能为空，请查询配置！");

		Bson filter = Filters.eq("_id", new ObjectId(id));
		final Map<String,String> resMap = new HashMap<String,String>() ;
        MongoDatabase database = mongoClient.getDatabase(dbName);
        GridFSBuckets.create(database).find(filter).forEach(new Block<GridFSFile>() {
            @Override
            public void apply(final GridFSFile gridFSFile) {
                resMap.put("md5", gridFSFile.getMD5()) ;
                resMap.put("extension", gridFSFile.getMetadata().getString("extension"));
            }
        });
        resMap.put("id", id) ;
        return resMap ;
	}
	

}
