
import json

from os import path, mkdir, chdir, system, environ, getcwd
from platform import system as platform_os

current_dir = getcwd()


def generate_spring():
  print('coming soon')


def generator_asp():
  f = open('settings.config')
  config = json.load(f)
  f.close()
  db_choice = config['db']
  if not path.exists('./build'):
    mkdir('./build')
  if not 'CLASSPATH' in environ.keys():
    cp_string = '.;%s/antlr4.jar;%%CLASSPATH%%' if platform_os() == 'Windows' else '.:%s/antlr4.jar:%%CLASSPATH%%'
    environ['CLASSPATH'] = cp_string % current_dir
  system('javac -d ./build ./parser/*.java ./base/*.java ./dotnet/*.java')
  chdir('./build')
  system('java Program -f app.txt -rn %s -db %d' % (config['root'], int(config['db'])))
  chdir('..')
