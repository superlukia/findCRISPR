/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg20151019;

import fastx.Fasta;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author shengjia
 */
public class Main1023 {
    public static void main(String[] args) throws Exception {
        String in=args[1];
        int length=23;
        BufferedReader inbr=new BufferedReader(new FileReader(in));
        String line="";
        HashMap<String,String> map=new HashMap<String, String>();
        while((line=inbr.readLine())!=null){
            String[] ds=line.split("\\t");
            map.put(ds[0], ds[2]);
        }
        System.err.println("output file loaded");
        inbr.close();
        for(int i=2;i<args.length;i++){
            String ref=args[i];
            if(ref.endsWith("chrY.fa")) continue;
            BufferedReader refbr=new BufferedReader(new FileReader(ref));
            List<Fasta> fas=fastx.Fasta.fasta2list(refbr);
            for(Fasta fa:fas){
                if(fa.getName().equalsIgnoreCase("chry") || fa.getName().length()>5) continue;
                String seq=fa.getSeq();
//                Set<String> keys=map.keySet();
//                List<String> removekeys=new ArrayList<String>();
//                
//                for(String key:keys){
//                    if(seq.contains(key)){
//                        removekeys.add(key);
//                    }
//                }
//                for(String key:removekeys){
//                    map.remove(key);
//                }
                for(int pos=0;pos<seq.length()-length+1;pos++){
                    String str=seq.substring(pos, pos+length).toUpperCase();
                    if(str.startsWith("CC") || str.endsWith("GG")){
                        map.remove(str);
                    }  
                    
//                    if((pos+length)<=seq.length()){
//                        if(seq.charAt(pos)=='C' && seq.charAt(pos+1)=='C'){
//                            String substr=seq.substring(pos, pos+length);
//                            map.remove(substr);
//                        }
//                    }
//                    if((pos-length+1)>=0){
//                        if(seq.charAt(pos)=='G' && seq.charAt(pos-1)=='G'){
//                            String substr=seq.substring(pos-length+1, pos+1);
//                            map.remove(substr);
//                        }
//                    }
                            
                }
            }
            System.err.println(ref+" processed");
        }
        for(String key:map.keySet()){
            String v=map.get(key);
            System.out.println(key+"\t"+v.split(";").length+"\t"+v);
        }
    }
}
