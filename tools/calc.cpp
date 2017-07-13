#include <iostream>
#include <sstream>
#include <string>
using namespace std;

struct Time {
    int Min, Max;
};

Time time[4];
int node[4];

string Aline(string s)
{
    return s + "\n";
}

string toString(int temp)
{
    stringstream stream;
    stream << temp;
    return stream.str();
}
string toString(double s)
{
    int temp;
    if ((int)(s * 10) % 10 >= 5)
        temp = (int)s + 1;
    else
        temp = (int)s;
    stringstream stream;
    stream << temp;
    return stream.str();
}

int main()
{
    cout << "Input Node information" << endl;
    int sum = 0;
    node[0] = 0;
    for (int i = 1; i <= 3; i++) {
        cin >> node[i];
        sum += node[i];
    }
    cout << "Input Time information" << endl;
    for (int i = 0; i < 3; i++)
        cin >> time[i].Min >> time[i].Max;

    cout << endl;

    for (int i = 1; i <= 3; i++) {
        for (int j = 0; j < 3; j++) {
            cout << "(" << (double)time[j].Min * 60.0 / node[i] << "," << (double)time[j].Max * 60.0 / node[i] << ")\t";
        }
        cout << endl;
    }
    cout << endl
         << endl
         << endl;

    int pos = 1;
    string s = "";
    for (int i = 1; i <= 3; i++) {
        s += Aline("Group" + toString(i) + ".nrofHosts=" + toString(node[i]));
        for (int j = 0; j < 3; j++) {
            //cout << "("<< (double)time[j].Min*60.0/node[i] <<","<< (double)time[j].Max*60.0/node[i] <<")\t";
            s += Aline("Events" + toString(pos) + ".hosts=" + toString(node[i - 1]) + "," + toString(node[i]));
            s += Aline("Events" + toString(pos) + ".interval=" + toString((double)time[j].Min * 60.0 / node[i]) + "," + toString((double)time[j].Max * 60.0 / node[i]));
            s += Aline("Events" + toString(pos) + ".tohosts=0," + toString(sum));
            pos++;
        }
    }
    cout << s << endl;
}