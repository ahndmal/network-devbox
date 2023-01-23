nc -k -l 4444 > filename.out

#nc -k -l 4444 | bash

echo "ls" >/dev/tcp/127.0.0.1/4444
