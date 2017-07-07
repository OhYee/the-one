#include  <iostream>
using namespace std;

struct Time {
    int Min, Max;
};

Time time[3];
int node[3];

int main()
{
    cout << "Input Node information" << endl;
    for (int i = 0; i < 3; i++)
        cin >> node[i];
    cout << "Input Time information" << endl;
    for (int i = 0; i < 3; i++)
        cin >> time[i].Min>> time[i].Max;

    cout << endl;
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            cout << "("<< (double)time[j].Min*60.0/node[i] <<","<< (double)time[j].Max*60.0/node[i] <<")\t";
        }
        cout << endl;
    }
}