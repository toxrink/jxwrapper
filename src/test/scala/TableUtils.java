import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import scala.Option;
import x.self.JxConst;
import x.utils.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表数据处理
 *
 * @author xw
 * @date 2019年4月26日
 */
public class TableUtils {
    private static Log LOG = JxUtils.getLogger(TableUtils.class);

    /**
     * {表名 => 添加的字段值数组}
     */
    private static final Map<String, Map<String, String>[]> TABLE_DATA_CACHE = new HashMap<String, Map<String, String>[]>();

    /**
     * {表名 => ip范围处理工具}
     */
    private static final Map<String, AreaTool> TABLE_AREA_TOOL_CACHE = new HashMap<String, AreaTool>();

    /**
     * {表名 => {join的字段值 => TABLE_DATA_CACHE中value的位置}}
     */
    private static final Map<String, Map<String, Integer>> TABLE_DATA_LINE_CACHE = new HashMap<String, Map<String, Integer>>();

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        init("C:\\Users\\admin\\Desktop\\iptable.csv");
        System.out.println(System.currentTimeMillis() - start);
        // System.out.println(getValueByJoin("area", "110000000000"));
        start = System.currentTimeMillis();
        String testIp = "194.147.7.255";
        System.out.println(getValueByBetweenIp("area", testIp));
        long last = System.currentTimeMillis();
        for (int i = 0; i < 5000000; i++) {
            if (i % 1000 == 0) {
                System.out.println(i + ":" + (System.currentTimeMillis() - last));
                last = System.currentTimeMillis();
            }
            getValueByBetweenIp("area", testIp);
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    // public static void main(String[] args) throws IOException {
    // File file = new File("C:\\Users\\admin\\Desktop\\iptable.csv");
    // List<String> lines = FileUtils.readLines(file, "UTF-8");
    // ArrayList<AreaInfo> ipList = new ArrayList<AreaInfo>();
    // for (Integer i = 2; i < lines.size(); i++) {
    // String[] sp = lines.get(i).split("\t");
    // long start = IpUtils.ipToNum(sp[1]);
    // long end = IpUtils.ipToNum(sp[2]);
    // ipList.add(new AreaInfo(i.toString(), i.toString(), sp[1], sp[2], start,
    // end));
    // }
    // AreaTool at = AreaUtils.loadArea(ipList);
    // int count = 0;
    // long last = System.currentTimeMillis();
    // while (count <= 100000) {
    // if (count % 1000 == 0) {
    // System.out.println(count + ":" + (System.currentTimeMillis() - last));
    // last = System.currentTimeMillis();
    // }
    // count = count + 1;
    // at.binarySearch("194.147.7.255");
    // }
    // }

    /**
     * 根据配置初始化表信息
     *
     * @param filePath 文件路径
     */
    public static void init(String filePath) {
        if (null == filePath) {
            LOG.warn("静态表配置文件路径为空");
            return;
        }
        LOG.debug("初始化静态表配置");
        for (String path : filePath.split(",")) {
            File file = new File(path);
            if (!file.exists()) {
                LOG.error("静态表配置不存在: " + path);
                continue;
            }
            try {
                List<String> lines = FileUtils.readLines(file, JxConst.UTF8);
                List<Integer> metaInfoSplit = new ArrayList<>();
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).charAt(0) == '{') {
                        metaInfoSplit.add(i);
                        // 添加break,取消单个文件多个table配置
                        break;
                    }
                }

                // 只有一个table配置
                if (metaInfoSplit.size() == 1) {
                    buildTableCache(lines, 0, lines.size());
                } else {
                    // 有多个table配置
                    int i = 1;
                    for (; i < metaInfoSplit.size(); i++) {
                        buildTableCache(lines, metaInfoSplit.get(i - 1), metaInfoSplit.get(i));
                    }
                    buildTableCache(lines, metaInfoSplit.get(i - 1), lines.size());
                }

            } catch (IOException e) {
                LOG.error("", e);
            }
        }
        LOG.debug("初始化静态表配置结束");
    }

    private static MetaInfo buildMetaInfo(List<String> lines, int metaLine, int columnLine) {
        Gson gson = new Gson();
        MetaInfo metaInfo = gson.fromJson(lines.get(metaLine), MetaInfo.class);
        String[] columns = lines.get(columnLine).split(metaInfo.sep);
        metaInfo.setColumns(columns);
        metaInfo.setAddIndex(new int[metaInfo.add.length]);
        for (int i = 0; i < metaInfo.add.length; i++) {
            for (int j = 0; j < columns.length; j++) {
                if (metaInfo.add[i].equals(columns[j])) {
                    metaInfo.getAddIndex()[i] = j;
                    break;
                }
            }
        }
        if (null != metaInfo.join) {
            for (int i = 0; i < columns.length; i++) {
                if (metaInfo.join.equals(columns[i])) {
                    metaInfo.setJoinIndex(i);
                    break;
                }
            }
        }
        if (null != metaInfo.betweenIp) {
            metaInfo.setBetweenIpIndex(new int[]{-1, -1});
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < columns.length; j++) {
                    if (metaInfo.betweenIp[i].equals(columns[j])) {
                        metaInfo.getBetweenIpIndex()[i] = j;
                        break;
                    }
                }
            }
        }
        return metaInfo;
    }

    private static void buildTableCache(List<String> lines, int startLine, int endLine) {
        MetaInfo metaInfo = buildMetaInfo(lines, startLine, startLine + 1);
        if (null == metaInfo) {
            return;
        }

        // 保存全部行数的add配置的字段和值
        @SuppressWarnings("unchecked")
        Map<String, String>[] dataMap = new HashMap[endLine - startLine + 1];
        // 保存单行add配置的字段和值
        Map<String, String> valueMap = null;
        // 保存join的字段和当前数据对应TABLE_DATA_LINE_CACHE中的位置
        Map<String, Integer> joinIndexMap = metaInfo.joinIndex == -1 ? new HashMap<String, Integer>(0)
                : new HashMap<String, Integer>(endLine - startLine + 1);
        // 保存betweenIp区域信息
        ArrayList<AreaInfo> areaInfoList = metaInfo.betweenIpIndex == null ? new ArrayList<>(0)
                : new ArrayList<>(endLine - startLine + 1);
        // 当前行数据在dataMap中的位置
        int dataIndex = 0;
        for (int i = startLine + 2; i < endLine; i++) {
            String[] data = lines.get(i).split(metaInfo.sep);
            if (data.length != metaInfo.getColumns().length) {
//                LOG.warn("数据字段个数和表字段个数不一致\n{}\n{}", metaInfo.getColumns(), data);
                continue;
            }
            valueMap = new HashMap<String, String>(metaInfo.getAdd().length);
            for (int index : metaInfo.addIndex) {
                valueMap.put(metaInfo.columns[index], data[index]);
            }
            dataMap[dataIndex] = valueMap;
            if (-1 != metaInfo.joinIndex) {
                joinIndexMap.put(data[metaInfo.joinIndex], dataIndex);
            }
            if (metaInfo.betweenIpIndex != null) {
                String startIp = data[metaInfo.betweenIpIndex[0]].trim();
                String endIp = data[metaInfo.betweenIpIndex[1]].trim();
                long startIpNumber = IpUtils.ipToNum(startIp);
                long endIpNumber = IpUtils.ipToNum(endIp);
                // areaCode和areaName为对应数据索引位置
                areaInfoList.add(new AreaInfo(String.valueOf(dataIndex),
                        String.valueOf(dataIndex), startIp, endIp, startIpNumber, endIpNumber));
//                areaInfoList.add(new AreaInfo("", String.valueOf(dataIndex), "", "", startIpNumber, endIpNumber));
            }
            dataIndex++;
        }
        TABLE_DATA_LINE_CACHE.put(metaInfo.getTable(), joinIndexMap);
        TABLE_DATA_CACHE.put(metaInfo.getTable(), dataMap);
        TABLE_AREA_TOOL_CACHE.put(metaInfo.getTable(), AreaUtils.loadArea(areaInfoList));
    }

    /**
     * 获取join的值
     *
     * @param table     表名
     * @param joinValue join的值
     * @return key不存在返回null
     */
    public static Map<String, String> getValueByJoin(String table, String joinValue) {
        Integer index = TABLE_DATA_LINE_CACHE.get(table).get(joinValue);
        if (null == index) {
            return null;
        }
        return TABLE_DATA_CACHE.get(table)[index];
    }

    /**
     * 获取betweenIp的值
     *
     * @param table 表名
     * @param ip    ip
     * @return key不存在返回null
     */
    public static Map<String, String> getValueByBetweenIp(String table, String ip) {
        if (!IpUtils.isIp(ip)) {
            return null;
        }
        AreaTool areaTool = TABLE_AREA_TOOL_CACHE.get(table);
        if (null == areaTool) {
            return null;
        }
        Option<AreaInfo> areaInfo = areaTool.binarySearch(ip);
        if (areaInfo.isEmpty()) {
            return null;
        }
        return TABLE_DATA_CACHE.get(table)[Integer.parseInt(areaInfo.get().areaCode())];
    }

    public static class MetaInfo {

        /**
         * 表名
         */
        private String table;

        /**
         * join的字段
         */
        private String join;

        /**
         * join字段在表中的位置
         */
        private int joinIndex = -1;

        /**
         * join后关联的字段
         */
        private String[] add;

        /**
         * 关联的字段,在表中的位置
         */
        private int[] addIndex;

        /**
         * 表字段名
         */
        private String[] columns;

        /**
         * 字段及数据分隔符
         */
        private String sep = "\t";

        private String[] betweenIp;

        private int[] betweenIpIndex;

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = table;
        }

        public String getJoin() {
            return join;
        }

        public void setJoin(String join) {
            this.join = join;
        }

        public String[] getAdd() {
            return add;
        }

        public void setAdd(String[] add) {
            this.add = add;
        }

        public String getSep() {
            return sep;
        }

        public void setSep(String sep) {
            this.sep = sep;
        }

        public String[] getColumns() {
            return columns;
        }

        public void setColumns(String[] columns) {
            this.columns = columns;
        }

        public int[] getAddIndex() {
            return addIndex;
        }

        public void setAddIndex(int[] addIndex) {
            this.addIndex = addIndex;
        }

        public int getJoinIndex() {
            return joinIndex;
        }

        public void setJoinIndex(int joinIndex) {
            this.joinIndex = joinIndex;
        }

        public String[] getBetweenIp() {
            return betweenIp;
        }

        public void setBetweenIp(String[] betweenIp) {
            this.betweenIp = betweenIp;
        }

        public int[] getBetweenIpIndex() {
            return betweenIpIndex;
        }

        public void setBetweenIpIndex(int[] betweenIpIndex) {
            this.betweenIpIndex = betweenIpIndex;
        }
    }
}
