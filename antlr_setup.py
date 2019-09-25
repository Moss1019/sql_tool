
from os import system, environ, chdir, getcwd, path
from urllib.request import urlopen
from platform import system as platform_os

current_dir = getcwd()

def download_antlr_jar():
    url = 'https://www.antlr.org/download/antlr-4.7.2-complete.jar'
    req = urlopen(url)
    f = open('antlr4.jar', 'wb')
    f.write(req.read())
    f.close()
    req.close()

def run_antlr():
    cp_string = '.;%s/antlr4.jar;%%CLASSPATH%%' if platform_os() == 'Windows' else '.:%s/antlr4.jar:%CLASSPATH%'
    environ['CLASSPATH'] = cp_string % current_dir
    print( environ['CLASSPATH'] )
    chdir('./parser')
    system('java -jar %s/antlr4.jar ./Definition.g4 -no-listener -visitor' % current_dir)
    chdir('..')

def compile_java():
    system('javac -d ./build ./parser/*.java ./*.java')
    chdir('./build')
    system('java Definition')
    chdir('..')

if __name__ == '__main__':
    if not path.exists('./antlr4.jar'):
        download_antlr_jar()
    run_antlr()
    compile_java()
