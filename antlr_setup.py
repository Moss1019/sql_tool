
from os import system, environ, chdir, getcwd, path
from urllib.request import urlopen

current_dir = getcwd()

def download_antlr_jar():
    url = 'https://www.antlr.org/download/antlr-4.7.2-complete.jar'
    req = urlopen(url)
    f = open('antlr4.jar', 'wb')
    f.write(req.read())
    f.close()
    req.close()

def run_antlr():
    environ['CLASSPATH'] = '.:%s/antlr4.jar:$CLASSPATH' % current_dir
    system('java -jar %s/antlr4.jar ./parser/Definition.g4 -no-listener -visitor' % current_dir)

def compile_java():
    system('javac -d ./build ./*.java ./parser/*.java')
    chdir('./build')
    system('java Definition')
    chdir('..')

if __name__ == '__main__':
    if not path.exists('./antlr4.jar'):
        download_antlr_jar()
    # run_antlr()
    compile_java()
