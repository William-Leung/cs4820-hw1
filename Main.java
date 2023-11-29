import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.ArrayList;

class Main {

    static int n;
    static boolean debug = false;

    // forbidden pairs
    static HashMap<Integer, HashMap<Integer,Integer>> forbidden_hr = new HashMap<Integer, HashMap<Integer,Integer>>();
    static HashMap<Integer, HashMap<Integer,Integer>> forbidden_rh = new HashMap<Integer, HashMap<Integer,Integer>>();

    // hospital preferences
    static ArrayList<ArrayList<Integer>> hosp_prefs = new ArrayList<ArrayList<Integer>>();
    // resident preferences 
    static HashMap<Integer, HashMap<Integer,Integer>> resident_prefs = new HashMap<Integer, HashMap<Integer,Integer>>();

    // final_hosp_matches[i] will contain the resident hospital i is finally matched
    // to
    static int[] final_hosp_matches = new int[2000];

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine());

        // reading in hospital preferences
        for (int i = 0; i < n; i++) {
            hosp_prefs.add(new ArrayList<Integer>());
            forbidden_hr.put(i, new HashMap<Integer,Integer>());

            String[] line = br.readLine().split(" ");
            for (int j = 0; j < n; j++) {
                int next_pref = Integer.parseInt(line[j]) - 1;
                hosp_prefs.get(i).add(next_pref);

                if(j > n/2 - 1) {
                    if(debug) { System.out.println("Forbidden_hr: " + i + ", " + next_pref);}

                    forbidden_hr.get(i).put(next_pref,1);
                }
        
            }
        }
        
        // reading in resident preferences
        for (int i = 0; i < n; i++) {
            resident_prefs.put(i,new HashMap<Integer,Integer>());
            forbidden_rh.put(i,new HashMap<Integer,Integer>());

            String[] line = br.readLine().split(" ");
            for (int j = 0; j < n; j++) {
                int next_pref = Integer.parseInt(line[j]) - 1;
                resident_prefs.get(i).put(next_pref, j);
                if(j > n/2 - 1) {
                    if(debug)  {System.out.println("Forbidden_rh: " + i + ", " + next_pref);}
                    forbidden_rh.get(i).put(next_pref,1);
                }
            }     
        }

       
        if(debug) {
        System.out.println("Hosp prefs");

        for(int i = 0; i < hosp_prefs.size(); i++) {
            for(int j = 0; j < hosp_prefs.get(0).size(); j++) {
                System.out.print((hosp_prefs.get(i).get(j)) + " ");
            }
            System.out.println();
        }

         System.out.println("Res prefs");

        for(int i = 0; i < resident_prefs.size(); i++) {
            for(int j = 0; j < resident_prefs.get(0).size(); j++) {
                System.out.print((resident_prefs.get(i).get(j) )+ " ");
            }
            System.out.println();
        }
    }

        br.close();

        // map of matched pairs. key: resident, value: hospital
        HashMap<Integer, Integer> resident_hosp_matches = new HashMap<Integer, Integer>();
        // currently matched hospitals
        HashSet<Integer> matched_hosps = new HashSet<Integer>();

        // next_proposal[i] is the the next index in the preference list that hospital i
        // is
        // going to propose to
        int[] next_proposal = new int[n];
        for (int i = 0; i < n; i++) {
            next_proposal[i] = 0;
        }

        // repeating G-S loop until all hospitals are matched
        int num_props = 0;

        boolean failed = false;

        boolean[] isMatched = new boolean[n];

        while (matched_hosps.size() < n && !failed) {
            for (int i = 0; i < n; i++) {
                if (!isMatched[i]) {
                    // resident the hospital wants to propose to in this round
                    if(next_proposal[i] <= n/2 - 1) {
                        int next_pref = hosp_prefs.get(i).get(next_proposal[i]);
                        if(forbidden_rh.get(next_pref).containsKey(i)) { 
                            next_proposal[i]++; 
                            continue;
                        }
                        Integer curr_match = resident_hosp_matches.get(next_pref);
                        if(curr_match != null) {
                            //if resident prefers current proposer to current match
                            if (resident_prefs.get(next_pref).get(curr_match) > resident_prefs.get(next_pref).get(i)) {
                                resident_hosp_matches.put(next_pref, i);
                                matched_hosps.add(i);
                                matched_hosps.remove(curr_match);
                                isMatched[curr_match] = false;
                                isMatched[i] = true;
                            }
                        } else {
                            resident_hosp_matches.put(next_pref, i);
                            matched_hosps.add(i);
                            isMatched[i] = true;
                        }
                        num_props++;
                        next_proposal[i]++;
                    } else {
                        failed = true;
                    }
                }
            }
        }
    

	    if(resident_hosp_matches.size() != n) {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
            bw.write("No\n");
            bw.close();
            return;
        }

        // need to do some processing to get hospital:resident key value pairs for
        // matches
        for (int res = 0; res < n; res++) {
            int hosp = resident_hosp_matches.get(res);
            final_hosp_matches[hosp] = res;
            if(forbidden_hr.get(hosp).containsKey(res) || forbidden_rh.get(res).containsKey(hosp)) {
                if(debug) {System.out.println("Failing: " + hosp + " and " + res);}
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
                 bw.write("No\n");
                bw.close();
                return;
            }
        }

        System.err.println("Total proposals: " + num_props);

        

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        bw.write("Yes\n");
        for (int hosp = 0; hosp < n; hosp++) {
            bw.write(String.valueOf(final_hosp_matches[hosp] + 1));
            bw.write('\n');
        }
        bw.flush();
        bw.close();

    }
}