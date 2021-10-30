/*Problem Link
https://practice.geeksforgeeks.org/problems/m-coloring-problem-1587115620/1#
*/
bool isValid(int cl, int ind, bool graph[101][101], int n, vector<int> &color)
{

    for (int i = 0; i < n; i++)
    {
        if (graph[ind][i] == 1 && ind != i && color[i] != -1)
        {
            if (color[i] == cl)
                return false;
        }
    }
    return true;
}

bool graphColoringUtil(int ind, bool graph[101][101], int m, int n, vector<int> &color)
{

    if (ind == n)
        return true;

    for (int i = 0; i < m; i++)
    {

        if (isValid(i, ind, graph, n, color))
        {

            color[ind] = i;
            if (graphColoringUtil(ind + 1, graph, m, n, color))
                return true;
            color[ind] = -1;
        }
    }
    return false;
}

bool graphColoring(bool graph[101][101], int m, int n)
{

    vector<int> color(n, -1);

    if (graphColoringUtil(0, graph, m, n, color))
        return true;

    return false;
}
