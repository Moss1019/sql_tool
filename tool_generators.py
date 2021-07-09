
import json

from os import path, mkdir, chdir, system, environ, getcwd
from platform import system as platform_os

current_dir = getcwd()


def _get_config():
  f = open('settings.config')
  config = json.load(f)
  f.close()
  return config


def _setup_folders():
  if not path.exists('./build'):
    mkdir('./build')
  if not 'CLASSPATH' in environ.keys():
    cp_string = '.;%s/antlr4.jar;%%CLASSPATH%%' if platform_os() == 'Windows' else '.:%s/antlr4.jar:%%CLASSPATH%%'
    environ['CLASSPATH'] = cp_string % current_dir


def generate_asp():
  _setup_folders()
  config = _get_config()
  db_choice = config['db']
  system('javac -d ./build ./parser/*.java ./base/*.java ./dotnet/*.java')
  chdir('./build')
  system('java Program -f app.txt -rn %s -db %d' % (config['root'], int(config['db'])))
  chdir('..')


def generate_spring():
  _setup_folders()
  config = _get_config()
  db_choice = config['db']
  system('javac -d ./build ./parser/*.java ./base/*.java ./java/*.java')
  chdir('./build')
  system('java Program -f app.txt -rn %s -db %d' % (config['root'], int(config['db'])))
  chdir('..')
