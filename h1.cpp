#include <iostream>
#include <unordered_map>
#include <unordered_set>
#include <algorithm>

using namespace std;

// resident preferences
int resident_prefs[2000][2000];
// hospital preferences
int hosp_prefs[2000][2000];
// final_hosp_matches[i] will contain the resident hospital i is finally matched to
int final_hosp_matches[2000];

int main()
{
  int n;
  cin >> n;

  // reading in hospital preferences
  for (int i = 0; i < n; i++)
  {
    for (int j = 0; j < n; j++)
    {
      int next_pref;
      scanf("%i", &next_pref);
      next_pref -= 1;
      hosp_prefs[i][j] = next_pref;
    }
  }

  // reading in resident preferences
  for (int i = 0; i < n; i++)
  {
    for (int j = 0; j < n; j++)
    {
      int next_pref;
      scanf("%i", &next_pref);
      next_pref -= 1;
      resident_prefs[i][j] = next_pref;
    }
  }

  // map of matched pairs. key: resident, value: hospital
  unordered_map<int, int> resident_hosp_matches;
  // currently matched hospitals
  unordered_set<int> matches_hosps;

  // next_proposal[i] is the the next index in the preference list that hospital i is
  // going to propose to
  int next_proposal[n];
  for (int i = 0; i < n; i++)
  {
    next_proposal[i] = 0;
  }

  // repeating G-S loop until all hospitals are matched
  while (matches_hosps.size() < n)
  {
    for (int i = 0; i < n; i++)
    {
      if (matches_hosps.find(i) == matches_hosps.end())
      {
        // resident the hospital wants to propose to in this round
        int next_pref = hosp_prefs[i][next_proposal[i]];

        // checking if the preferred resident is already matched
        if (resident_hosp_matches.find(next_pref) != resident_hosp_matches.end())
        {
          int curr_match = resident_hosp_matches[next_pref];
          int pref_curr = distance(resident_prefs[next_pref], find(begin(resident_prefs[next_pref]), end(resident_prefs[next_pref]), curr_match));
          int pref_i = distance(resident_prefs[next_pref], find(begin(resident_prefs[next_pref]), end(resident_prefs[next_pref]), i));

          if (pref_curr > pref_i)
          {
            resident_hosp_matches[next_pref] = i;
            matches_hosps.insert(i);
            matches_hosps.erase(curr_match);
          }
        }
        else
        {
          resident_hosp_matches[next_pref] = i;
          matches_hosps.insert(i);
        }
        next_proposal[i]++;
      }
    }
  }

  // need to do some processing to get hospital:resident key value pairs for matches
  for (int res = 0; res < n; res++)
  {
    int hosp = resident_hosp_matches[res];
    final_hosp_matches[hosp] = res;
  }

  cout << "Yes\n";

  for (int hosp = 0; hosp < n; hosp++)
  {
    // printf is ideal, but I'm being lazy because C++ is fast:)
    cout << (final_hosp_matches[hosp] + 1) << '\n';
  }
}
