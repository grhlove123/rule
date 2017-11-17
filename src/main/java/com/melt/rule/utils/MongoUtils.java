package com.melt.rule.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.melt.rule.bean.RuleContext;
import com.melt.rule.exception.RuleRuntimeException;
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
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import static com.mongodb.client.model.Projections.*;

/**
 * mongod 操作工具类
 * author: melt
 * date: 2017-11-09
 */
public class MongoUtils {
	private static final Logger logger = LoggerFactory.getLogger(MongoUtils.class);
	public static MongoClient mongoClient;
//	private static GridFSBucket gridFSBucket ;

    /**
     * 检查不能为空
     * @param dbName    数据库名称
     * @param colName   集合名称
     */
    public static void checkNotNull(String dbName,String colName){
        Assert.notNull(dbName,"数据库名称不能为空！");
        Assert.notNull(colName,"集合名称不能为空！");
//        MongoClient mongoClient = ThreadLocalHolder.get(RuleConstant.MONGO_CLIENT_KEY,MongoClient.class) ;
        Assert.notNull(mongoClient,"mongoClient不能为空，请查询配置！");
    }
	/**
	 * 查找对象 - 根据主键_id
     * @param dbName    数据库名称
     * @param colName   集合名称
	 * @param id        主键
	 * @return
	 */
	public static Document queryById(String dbName,String colName, String id) {
        checkNotNull(dbName,colName);
        Assert.notNull(id,"查询主键不能为空！");
        ObjectId _idobj = new ObjectId(id);;
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
		return mongoClient.getDatabase(dbName).getCollection(colName).find(filter).into(new ArrayList<Document>()) ;
	}

    /**
     * 条件查询(分页)
     * @param dbName    数据库名称
     * @param colName   集合名称
     * @param filter    条件
     * @param pageNo    页码	1开始
     * @param pageSize  页大小
     * @return
     */
	public static List<Document> queryByPage(String dbName, String colName, Bson filter, int pageNo, int pageSize) {
        checkNotNull(dbName,colName);
		Bson orderBy = new BasicDBObject("_id", 1);
		return mongoClient.getDatabase(dbName).getCollection(colName).find(filter)
                .sort(orderBy).skip((pageNo - 1) * pageSize).limit(pageSize).into(new ArrayList<>()) ;
	}

    /**
     * 条件查询(分页)
     * @param dbName    数据库名称
     * @param colName   集合名称
     * @param cols      显示列	可以为空
     * @param filter    条件		可以为空
     * @param orderBy   排序		可以为空
     * @param pageNo    页码		大于0分页
     * @param pageSize  页大小	大于0分页
     * @return
     */
    public static List<Document> query(String dbName,String colName,List<String> cols,Bson filter,
                                              Bson orderBy,int pageNo, int pageSize) {
        checkNotNull(dbName,colName);
        FindIterable<Document> fi = null ;
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
     * @param hideCols  隐藏列
     * @param filter    条件
     * @param orderBy   排序
     * @param pageNo    页码
     * @param pageSize  页大小
     * @return  List<Document>
     */
    public static List<Document> query2(String dbName,String colName,List<String> hideCols,Bson filter,
                                                     Bson orderBy,int pageNo, int pageSize) {
        checkNotNull(dbName,colName);
        FindIterable<Document> fi = null ;
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
		return query(dbName,colName,null, filter, orderBy, 0, 0);
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
        mongoClient.getDatabase(dbName).getCollection(colName).insertOne(doc);
		return doc.getObjectId("_id").toString() ;
	}


    /**
     * 通过ID主键删除
     * @param dbName    数据库名称
     * @param colName   集合名称
     * @param id        主键
     * @return int
     */
	public static int deleteById(String dbName,String colName, String id) {
        checkNotNull(dbName,colName);
		int count = 0;
		ObjectId _id =  new ObjectId(id);

		Bson filter = Filters.eq("_id", _id);
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

	/**
	 * 构建过滤条件
	 * @param funObj	函数定义对象
	 * @param context	规则上下文
	 * @return	条件
	 */
	public static Bson buildWhere(JSONObject funObj, RuleContext context){
		if (CollectionUtils.isEmpty(funObj)){
			return null;
		}
		JSONObject where = funObj.getJSONObject("where") ;
		if (CollectionUtils.isEmpty(where)){
			return null;
		}

		if (where.size() > 1){
			throw new RuleRuntimeException("the where must be one of [$and,$or]");
		}
		logger.debug("source where json: {}",where);
		recursiveReplace(where,context) ;
		logger.debug("after the replacement where json: {}",where);

		return Document.parse(where.toJSONString()) ;

	}

	/**
	 * 构建排序
	 * @param funObj	函数定义对象
	 * @param context	规则上下文
	 * @return	排序
	 */
	public static Bson buildOrderBy(JSONObject funObj, RuleContext context){
		if (CollectionUtils.isEmpty(funObj)){
			return null;
		}
		JSONObject orderBy = funObj.getJSONObject("orderBy") ;
		if (CollectionUtils.isEmpty(orderBy)){
			return null;
		}


		logger.debug("source orderBy json: {}",orderBy);
		recursiveReplace(orderBy,context) ;
		logger.debug("after the replacement orderBy json: {}",orderBy);

		return Document.parse(orderBy.toJSONString()) ;

	}

	/**
	 * 把变量占位符替换成相应的值
	 * @param where
	 * @param context
	 */
	public static void recursiveReplace(JSONObject where, RuleContext context){
		for (String key : where.keySet()){
			Object val = where.get(key) ;
			if (val instanceof String && ((String) val).startsWith("#")){
				where.put(key,JsonUtils.getValue((String) val,context)) ;
			} else if (val instanceof JSONObject){
				recursiveReplace((JSONObject) val,context);
			} else if (val instanceof JSONArray){
				JSONArray jsonArray = (JSONArray)val ;
//				jsonArray.forEach(k -> {
//					recursiveReplace((JSONObject)k,context);
//				});
				jsonArray.stream().filter(k -> k instanceof JSONObject).forEach(s -> recursiveReplace((JSONObject)s,context));
			}

		}
	}

	public static void main(String[] ars) throws Exception{
//		Bson bson = and(or(eq("price", 0.99), eq("price", 1.99),
//				or(eq("sale", true), lt("qty", 20))));
//		System.out.println(bson);

		InputStream inputStream = new FileInputStream("E:\\nantian\\人寿\\新一代\\规则\\rhguo\\hello.json") ;
		String srule = IOUtils.toString(inputStream) ;
		JSONObject jsonObject = JSONObject.parseObject(srule) ;

		System.out.println(jsonObject.getJSONObject("ccc") + "a----");
		System.out.println(jsonObject.getIntValue("111"));
		JSONArray jsonArray = jsonObject.getJSONArray("funs") ;
		JSONObject where = ((JSONObject)jsonArray.get(0)).getJSONObject("where") ;
		int index = 0;
		System.out.println(++index);

//		jsonArray.forEach(k ->{
//			where = ((JSONObject)k).getJSONObject("where") ;
//
//			System.out.println(where.toJSONString());
//			where.forEach((k1,v1) -> {
//				System.out.println(k1 + ":" + v1);
//				v1 = 2;
//			});
//			System.out.println(where.toJSONString());
//		});

		RuleContext context = new RuleContext();
		JSONObject inputVar = new JSONObject();
		inputVar.put("p1",11) ;
		inputVar.put("p2",21) ;
		inputVar.put("sale",false) ;
		inputVar.put("qty",3) ;
		context.setInputVar(inputVar);
//		recursiveReplace(where,context);
//		System.out.println(where);
//		System.out.println(Document.parse(where.toJSONString()));
		buildWhere(where,context);
	}

}
