import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

class Main {

    static int n;
    // resident preferences
    static ArrayList<ArrayList<Integer>> resident_prefs = new ArrayList<ArrayList<Integer>>();
    // hospital preferences
    static ArrayList<ArrayList<Integer>> hosp_prefs = new ArrayList<ArrayList<Integer>>();
    // final_hosp_matches[i] will contain the resident hospital i is finally matched
    // to
    static int[] final_hosp_matches = new int[2000];

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine());

        // reading in hospital preferences
        for (int i = 0; i < n; i++) {
            hosp_prefs.add(new ArrayList<Integer>());
            String[] line = br.readLine().split(" ");
            for (int j = 0; j < n; j++) {
                int next_pref = Integer.parseInt(line[j]) - 1;
                hosp_prefs.get(i).add(next_pref);
            }
        }

        // reading in resident preferences
        for (int i = 0; i < n; i++) {
            resident_prefs.add(new ArrayList<Integer>());
            String[] line = br.readLine().split(" ");
            for (int j = 0; j < n; j++) {
                int next_pref = Integer.parseInt(line[j]) - 1;
                resident_prefs.get(i).add(next_pref);
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
        while (matched_hosps.size() < n) {
            for (int i = 0; i < n; i++) {
                if (!matched_hosps.contains(i)) {
                    // resident the hospital wants to propose to in this round
                    int next_pref = hosp_prefs.get(i).get(next_proposal[i]);

                    // checking if the preferred resident is already matched
                    if (resident_hosp_matches.containsKey(next_pref)) {
                        int curr_match = resident_hosp_matches.get(next_pref);
                        if (resident_prefs.get(next_pref).indexOf(curr_match) > resident_prefs.get(next_pref)
                                .indexOf(i)) {
                            resident_hosp_matches.put(next_pref, i);
                            matched_hosps.add(i);
                            matched_hosps.remove(curr_match);
                        }
                    } else {
                        resident_hosp_matches.put(next_pref, i);
                        matched_hosps.add(i);
                    }
                    num_props++;
                    next_proposal[i]++;
                }
            }
        }

        // need to do some processing to get hospital:resident key value pairs for
        // matches
        for (int res = 0; res < n; res++) {
            int hosp = resident_hosp_matches.get(res);
            final_hosp_matches[hosp] = res;
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
