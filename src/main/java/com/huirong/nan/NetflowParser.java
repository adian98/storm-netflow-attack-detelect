package com.huirong.nan;

/**
 * Created by nanhuirong on 16-7-17.
 */
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;


/**
 *
 * @author yaoxin
 *
 * 2015年12月1日
 */
public class NetflowParser {

    public final static String TJUT_INPUT_DIR_PREFIX = "/data/tjut_netflow/netflow_binary/";
    public final static String TJUT_TMP_OUTPUT_DIR_PREFIX = "/data/tjut_netflow/netflow_tmp/";
    public final static String TJUT_OUTPUT_DIR_PREFIX = "/data/tjut_netflow/netflow_txt/";
    public final static String EDU_INPUT_DIR_PREFIX = "/data/edu_netflow/netflow_binary/";
    public final static String EDU_TMP_OUTPUT_DIR_PREFIX = "/data/edu_netflow/netflow_tmp/";
    public final static String EDU_OUTPUT_DIR_PREFIX = "/data/edu_netflow/netflow_txt/";
    public final static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmm");

    public static void parseAndCleanup(String inDir, String tmpDir, String outDir, NetflowParser p) throws Exception{

        if(!inDir.endsWith("/")){
            inDir += "/";
        }
        File[] ls = new File(inDir).listFiles();

        List<File> tmp = new ArrayList<File>();
        for(File file : ls){
            if(file.isFile() && file.getName().substring(0, 6).equals("nfcapd")){
                tmp.add(file);
            }
        }

        File[] target = tmp.toArray(new File[0]);
        Arrays.sort(target, p.new NfComparator());

        for(File file : target){
            if(file.isFile() && file.getName().substring(0, 6).equals("nfcapd")){
                String tmpOutputDir = tmpDir + file.getName() + ".txt";
                String outputDir = outDir + file.getName() + ".txt";

                String cmd = "/home/yaoxin/software/nfdump-1.6.13/bin/nfdump -r " + inDir + file.getName()
                        + " -o extended > " + tmpOutputDir;

                Process process = Runtime.getRuntime().exec(new String[]{"bash","-c",cmd});
                process.waitFor();
                file.delete();

                String cmd2 = "mv " + tmpOutputDir + " " + outputDir;
                Process process2 = Runtime.getRuntime().exec(new String[]{"bash","-c", cmd2});
                process2.waitFor();

            }
        }
    }

    public static void main(String[] args) throws Exception {

        String mode = args[0];

        NetflowParser p = new NetflowParser();

        if(mode != null && mode.equals("test")){
            String inDir = args[1];
            String tmpDir = args[2];
            String outDir = args[3];
            parseAndCleanup(inDir,tmpDir, outDir, p);
        }
        else if(mode != null && mode.equals("production")){

            String source = args[1];

            if(source != null && source.equals("tjut")){
                while(true){
                    Calendar calendar = Calendar.getInstance();
                    String dateStr = sdf1.format(calendar.getTime());

                    String tjutInputDir = TJUT_INPUT_DIR_PREFIX + dateStr;
                    File tmp = new File(tjutInputDir);
                    if(tmp != null && tmp.exists() && tmp.listFiles() != null && tmp.listFiles().length >= 1){
                        parseAndCleanup(tjutInputDir,TJUT_TMP_OUTPUT_DIR_PREFIX, TJUT_OUTPUT_DIR_PREFIX, p);
                    }

                    Thread.sleep(30000);
                }
            }
            else if(source != null && source.equals("edu")){
                while(true){
                    Calendar calendar = Calendar.getInstance();
                    String dateStr = sdf1.format(calendar.getTime());

                    String eduInputDir = EDU_INPUT_DIR_PREFIX + dateStr;
                    File tmp2 = new File(eduInputDir);
                    if(tmp2 != null && tmp2.exists() && tmp2.listFiles() != null && tmp2.listFiles().length >= 1){
                        parseAndCleanup(eduInputDir,EDU_TMP_OUTPUT_DIR_PREFIX, EDU_OUTPUT_DIR_PREFIX, p);
                    }

                    Thread.sleep(30000);
                }
            }


        }

    }

    class NfComparator implements Comparator<File>{

        public int compare(File arg0, File arg1) {
            try {
                String date1 = arg0.getName().substring(7, 19);
                String date2 = arg1.getName().substring(7, 19);

                return sdf2.parse(date1).compareTo(sdf2.parse(date2));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return 0;
        }

    }

}

