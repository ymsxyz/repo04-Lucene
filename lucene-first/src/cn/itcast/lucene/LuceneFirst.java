package cn.itcast.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class LuceneFirst {

    private FSDirectory directory;
    private IndexWriter indexWriter;

    @Before
    public void init() throws IOException {
        //把索引保存在磁盘
        directory = FSDirectory.open(new File("D:\\JavaTest\\temp\\index").toPath());
        //2.1使用自定义分析器
        indexWriter = new IndexWriter(directory, new IndexWriterConfig(new IKAnalyzer()));
    }

    //1.创建索引
    @Test
    public void creatIndex() throws Exception {
        //1.创建一个Director对象指定索引库保存的位置
        //把索引库保存在内存中
        //RAMDirectory directory = new RAMDirectory();

        //2.基于Directory对象创建一个IndexWriter对象
        //IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig());

        //3.读取磁盘上的文件,对应每个文件闯进啊一个文档对象
        File dir = new File("D:\\java黑马学习资料\\就业班资料\\lesson\\lucene\\资料\\searchsource");
        for (File f : dir.listFiles()) {
            //文件名
            String fileName = f.getName();
            //文件内容
            String fileContent = FileUtils.readFileToString(f);
            //文件路径
            String filePath = f.getPath();
            //文件大小
            long fileSize = FileUtils.sizeOf(f);
            //创建文件名域
            //第一个参数:域名称,第二个参数:域的内容,第三个参数:是否存储
            Field fileNameField = new TextField("filename", fileName, Field.Store.YES);
            //文件内容域
            TextField fileContentField = new TextField("content", fileContent, Field.Store.YES);
            //文件路径域(只存储)
            TextField filePathField = new TextField("path", filePath, Field.Store.YES);
            //文件大小域
            TextField fileSizeField = new TextField("size", fileSize + "", Field.Store.YES);

            //创建document对象:添加域
            Document document = new Document();
            document.add(fileNameField);
            document.add(fileContentField);
            document.add(filePathField);
            document.add(fileSizeField);

            //创建索引,并写入索引库
            indexWriter.addDocument(document);
        }
        //关闭indexWriter
        indexWriter.close();
    }

    //2.查询索引
    //第一步：创建一个Directory对象，也就是索引库存放的位置。
    //第二步：创建一个indexReader对象，需要指定Directory对象。--抽象类:通过DirectoryReader类获取
    //第三步：创建一个indexsearcher对象，需要指定IndexReader对象
    //第四步：创建一个TermQuery对象，指定查询的域和查询的关键词。
    //第五步：执行查询。
    //第六步：返回查询结果。遍历查询结果并输出。
    //第七步：关闭IndexReader对象
    @Test
    public void searchIndex() throws IOException {

        FSDirectory directory = FSDirectory.open(new File("D:\\JavaTest\\temp\\index").toPath());
        IndexReader indexReader = DirectoryReader.open(directory);
        //创建indexsearcher对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //创建查询
        Query query = new TermQuery(new Term("filename", "apache"));
        //执行查询:参数(查询对象;结果返回的最大值)
        TopDocs topdocs = indexSearcher.search(query, 11);
        //总条数
        System.out.println("总条数:" + topdocs.totalHits);
        //遍历查询结果
        //topDocs.scoreDocs存储了document的对象id
        for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
            //scoreDoc.doc属性就是document对象的id
            //根据document的id找到document对象
            Document document = indexSearcher.doc(scoreDoc.doc);

            System.out.println(document.get("filename"));
            System.out.println(document.get("path"));
            System.out.println(document.get("size"));
            System.out.println("-----------------------------");
        }
        //关闭indexReader对象
        indexReader.close();
    }

    //删除全部索引
    @Test
    public void deleteAllIndex() throws Exception {
        //删除全部索引
        indexWriter.deleteAll();
        //关闭indexWriter
        indexWriter.close();
    }

    //更具查询条件删除索引
    @Test
    public void deleteIndexByQuery() throws Exception {
        Query query = new TermQuery(new Term("filename", "apache"));
        indexWriter.deleteDocuments(query);
        indexWriter.close();
    }
}

















