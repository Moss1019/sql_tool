
from antlr_setup import download_antlr_jar, run_antlr, compile_java, run_java

def get_choice():
    print('Select an option to run')
    print('0. exit')
    print('1. download antlr4 jar file')
    print('2. run antlr on a specified g4 grammar file')
    print('3. compile all java file in the root and ./parser directories')
    print('4. run java language application')
    choise = -1
    try:
        choise = int(input())
    except:
        pass
    return choise

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
        antlr_file = input('enter the name of the file containing the grammar\n => ')
        if len(antlr_file) == 0:
            antlr_file = "Definition"
        print('running antlr...')
        run_antlr(antlr_file)
    elif choice == 3:
        print('compiling antlr and application .java files...')
        compile_java()
    elif choice == 4:
        file_name = input('enter the name of the file containing the definition\n => ')
        if len(file_name) == 0:
            file_name = 'test.txt'
        package_name = input('enter the root package name\n => ')
        if len(package_name) == 0:
            package_name = 'com.example'
        db_user = input('enter the app database user name\n => ')
        if len(db_user) == 0:
            db_user = 'default_user'
        print('running java language app...')
        run_java(file_name, package_name, db_user)
    else:
        continue
