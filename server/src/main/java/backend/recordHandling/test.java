package backend.recordHandling;

import backend.Indexing.*;
import backend.Utilities.BaseTable;
import backend.Utilities.GroupedTable;
import backend.Utilities.JoinedTable;
import backend.Utilities.Table;
import backend.config.Config;
import backend.databaseModels.JoinModel;
import backend.databaseModels.aggregations.Aggregator;
import backend.databaseModels.aggregations.AggregatorSymbol;
import backend.databaseModels.conditions.*;
import backend.exceptions.NoIndexException;
import backend.exceptions.recordHandlingExceptions.*;
import backend.service.CatalogManager;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.chrono.ThaiBuddhistChronology;
import java.util.*;

@Slf4j
public class test {
    public static ArrayList<String> types;
    public static void TreeNodeTest(){
        TreeNode node = new TreeNode(true, types);
        byte[] bytes1 = {0, 0, 0, 1};
        byte[] bytes2 = {0, 0, 0, 2};
        byte[] bytes3 = {0, 0, 0, 3};

        Key key1 = new Key(bytes1, types);
        Key key2 = new Key(bytes2, types);
        Key key3 = new Key(bytes3, types);

        node.insertInLeaf(key1, 1);

        TreeNode node1 = new TreeNode(true, types);
        node1.insertInLeaf(key3, 3);

        System.out.println(node);
        System.out.println(node1);

        node.join(node1, key2);
        System.out.println(node);

        System.out.println(node.popBackKey());
        System.out.println(node.popSecondToLastPointer());
        System.out.println(node);
//        try{
//            node.removeKey(key2);
//            node.removeKey(key3);
//            node.removeKey(key3);
//            node.removeKey(key1);
//        }catch (Exception e){
//            System.out.println("nope");
//        }
//        System.out.println(node);
    }

    public static void BtreeDeleteTest() throws IOException, RecordNotFoundException, KeyAlreadyInTreeException, KeyNotFoundException {
        String filename = Config.getDbRecordsPath() + File.separator + "test.index.bin";
        System.out.println(filename);
        BPlusTree tree = new BPlusTree(types, filename);
        tree.createEmptyTree();
        Random r = new Random();
        ArrayList<Integer> nums = new ArrayList<>(), pointers = new ArrayList<>();
        ArrayList<Key> keys = new ArrayList<>();
        int n = 1000;
        for(int i = 0; i < n; i++){
            ByteBuffer buffer = ByteBuffer.allocate(4);
            int num = r.nextInt() % 10000, pointer = r.nextInt() % 10000;
            while(nums.contains(num)){
                num = r.nextInt() % 10000;
            }
            while(pointers.contains(pointer)){
                pointer = r.nextInt() % 10000;
            }
            nums.add(num);
            pointers.add(pointer);
            buffer.putInt(num);
            Key key = new Key(buffer.array(), types);
            keys.add(key);
            tree.insert(key, pointer);
            //tree.printTree();
        }

        //System.out.println(nums);
        //System.out.println(pointers);

        ArrayList<Integer> finds = new ArrayList<>();

        tree.printTree();

        for(int i = 0; i < n; i++){
            tree.delete(keys.get(i));
            //tree.printTree();
        }

//        for(int i = 0; i < n; i++){
//            finds.add(tree.find(keys.get(i)));
//        }
//
//        System.out.println(finds);
//
//        if(pointers.equals(finds)){
//            System.out.println("Nice");
//        }

        //System.out.println(keys);
        //System.out.println(pointers);
        tree.insert(keys.get(0), pointers.get(0));
        tree.printTree();

        tree.close();
    }

    public static void testUniqueIndexManager() throws IOException, KeyAlreadyInTreeException, KeyNotFoundException {
        UniqueIndexManager manager = new UniqueIndexManager("asd", "asd", "asd");

        ArrayList<String> values = new ArrayList<>();
        values.add("1");

        manager.insert(values, 101);
        System.out.println(manager.isPresent(values));
        System.out.println(manager.findLocation(values));
        manager.delete(values);
        System.out.println(manager.isPresent(values));

        manager.close();
    }

    public static void scanTest() throws IOException, InvalidReadException {
        RecordReader io = new RecordReader("master", "scan");
        ArrayList<String> columns = new ArrayList<>();
        columns.add("id");
        columns.add("nev");
        System.out.println(io.scan(columns));

        ArrayList<Integer> pointers = new ArrayList<>();
        pointers.add(1);
        pointers.add(2);
        System.out.println(io.scanLines(pointers));
    }

    public static void rangeQueryTest() throws UndefinedQueryException, IOException {
        UniqueIndexManager manager = new UniqueIndexManager("master", "scan2", "id");
        System.out.println(manager.rangeQuery(1,3, true, true));
        System.out.println(manager.rangeQuery(1,3, false, true));
        System.out.println(manager.rangeQuery(1,3, true, false));
        System.out.println(manager.rangeQuery(-100,300, false, false));

        System.out.println(manager.lesserQuery(3, false));
        System.out.println(manager.greaterQuery(1, true));
    }

    public static void tableTest() throws IOException {
        ArrayList<Condition> conds = new ArrayList<>();
        conds.add(new Equation("people.id", Operator.LESS_THAN, "4"));
        ArrayList<String> args = new ArrayList<>();
        args.add("23");
        args.add("50");
        conds.add(new FunctionCall("people", "people.age", Function.BETWEEN, args));
        conds.add(new Equation("people.name", Operator.EQUALS, "daniel"));
        BaseTable people = new BaseTable("master", "people", conds);
        //BaseTable people = new BaseTable("master", "people");
        ArrayList<String> columns = new ArrayList<>();
        columns.add("people.name");
        columns.add("people.age");
        columns.add("people.height");
        people.projection(columns);

        ArrayList<Aggregator> aggregators = new ArrayList<>();
        aggregators.add(new Aggregator("people.height", AggregatorSymbol.AVG));
        aggregators.add(new Aggregator("people.height", AggregatorSymbol.MAX));

        //people.aggregation(aggregators);

        ArrayList<String> wantedColumns = new ArrayList<>();
        wantedColumns.add("people.age");
        GroupedTable groupedPeople = people.groupBy(wantedColumns);

        columns = new ArrayList<>();
        columns.add("AVG(people.height)");

        groupedPeople.aggregation(aggregators);

        groupedPeople.projection(columns);
        groupedPeople.printState();

        people.printState();
    }

    public static void joinTest() throws IOException {
        ArrayList<Condition> conds = new ArrayList<>();
        conds.add(new Equation("user.id", Operator.LESS_THAN_OR_EQUAL_TO, "4"));
        BaseTable users = new BaseTable("master", "user", conds);
        BaseTable albums = new BaseTable("master", "album");

        users.printState();
        albums.printState();

        ArrayList<JoinModel> joinModels = new ArrayList<>();
        joinModels.add(new JoinModel("user", "user.id", "album", "album.uid"));
        //joinModels.add(new JoinModel("album", "album.uid", "album", "album.uid"));

        ArrayList<Table> tables =new ArrayList<>();
        tables.add(users);
        tables.add(albums);
        tables.add(albums);

        JoinedTable joinedTable = (JoinedTable) JoinedTable.join(tables, joinModels);

        joinedTable.printState();
    }
    public static void main(String[] args) throws IOException, KeyAlreadyInTreeException, KeyNotFoundException, InvalidReadException, UndefinedQueryException, NoIndexException {
//        byte[] bytes = {0,0,0,1,0,0,0,1,1};
        types = new ArrayList<>();
        types.add("int");
        //TreeNodeTest();
        //BtreeDeleteTest();
        //testUniqueIndexManager();
        //scanTest();
        //rangeQueryTest();
        //tableTest();
        //joinTest();

    }
}
