
from os import system, environ, chdir, getcwd, path, mkdir
from urllib.request import urlopen
from platform import system as platform_os

current_dir = getcwd()

def download_antlr_jar():
    if path.exists('./antlr4.jar'):
        return
    url = 'https://www.antlr.org/download/antlr-4.7.2-complete.jar'
    req = urlopen(url)
    f = open('antlr4.jar', 'wb')
    total = 0
    while True:
        chunk = req.read(1024)
        total += len(chunk)
        print('\rdownloaded %d bytes' % total, end='')
        if not chunk:
            break
        f.write(chunk)
    f.close()
    req.close()

def run_antlr(grammar_file):
    chdir('./parser')
    system('java -jar %s/antlr4.jar ./%s.g4 -no-listener -visitor' % (current_dir, grammar_file))
    chdir('..')

def compile_java():
    if not path.exists('./build'):
        mkdir('./build')
    cp_string = '.;%s/antlr4.jar;%%CLASSPATH%%' if platform_os() == 'Windows' else '.:%s/antlr4.jar:%CLASSPATH%'
    environ['CLASSPATH'] = cp_string % current_dir
    system('javac -d ./build ./parser/*.java ./*.java')

def run_java():
    if not path.exists('./output'):
        mkdir('./output')
    chdir('./build')
    system('java Definition')
    chdir('..')

if __name__ == '__main__':
    if not path.exists('./antlr4.jar'):
        download_antlr_jar()
    run_antlr('Definition')
    compile_java()
    run_java()
