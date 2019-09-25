
from antlr_setup import download_antlr_jar, run_antlr, compile_java, run_java

def get_choice():
    print('Select an option to run')
    print('0. exit')
    print('1. download antlr4 jar file')
    print('2. run antlr on a specified g4 grammar file')
    print('3. compile all java file in the root and ./parser directories')
    print('4. run java language application')
    return int(input())

is_running = True
while is_running:
    choice = get_choice()
    if choice == 0:
        print('exiting...')
        is_running = False
    elif choice == 1:
        print('downloading antlr jar file...')
        download_antlr_jar()
    elif choice == 2:
        antlr_file = input('enter the name for the file, default "Definition"\n => ')
        if len(antlr_file) == 0:
            antlr_file = "Definition"
        print('running antlr...')
        run_antlr(antlr_file)
    elif choice == 3:
        print('compiling antlr and applicaiton .java files...')
        compile_java()
    elif choice == 4:
        print('running java language app...')
        run_java()
    else:
        continue