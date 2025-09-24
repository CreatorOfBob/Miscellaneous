#include <iostream>
#include <vector>
#include <random>
#include <chrono>

int main()
{
    using namespace std;

    cout << "Enter board size: ";
    int n;
    cin >> n;

    vector<vector<char>> board(n, vector<char>(n, ' '));

    cout << "Enter number of active cells between 0 and " << n*n << ": ";
    int a;
    cin >> a;

    unsigned seed = chrono::system_clock::now().time_since_epoch().count();
    mt19937 gen(seed);
    uniform_int_distribution<int> dist(0, n - 1);

    for (int i = 0; i < a; i++) {
        int x = dist(gen);
        int y = dist(gen);
        board[x][y] = 'O';
    }

    // Print initial board
    for (const auto& row : board) {
        for (char cell : row) {
            cout << cell << " ";
        }
        cout << "\n";
    }
    cin.get();

    vector<vector<int>> neighbors(n, vector<int>(n, 0));

    while (true) {
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                int count = 0;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i == 0 && j == 0) continue;
                        if (r+i >= 0 && r+i < n && c+j >= 0 && c+j < n && board[r+i][c+j] == 'O') {
                            count++;
                        }
                    }
                }
                neighbors[r][c] = count;
            }
        }

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                char cell = board[r][c];
                int count = neighbors[r][c];
                if (cell == 'O') {
                    if (count < 2 || count > 3) board[r][c] = ' ';
                } else {
                    if (count == 3) board[r][c] = 'O';
                }
            }
        }

        cin.get();
        for (const auto& row : board) {
            for (char cell : row) {
                cout << cell << " ";
            }
            cout << "\n";
        }
    }

    return 0;
}
