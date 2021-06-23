
from os import path, mkdir, chdir, system, environ, getcwd
from platform import system as platform_os

current_dir = getcwd()


def get_data_option():
  options = [
    '1. In memory',
    '2. SQL database', # cs => Sql Server, java => MySql
    '3. Document based' # cs => TableStorage, java => Firebase
  ]
  print('\tSelect a database option')
  [print(option) for option in options]
  choice = input()
  try:
    return int(choice)
  except:
    print('defaulting to in memory...')
    return 1


def generate_spring():
  print('coming soon')


def generator_asp():
  db_choice = get_data_option()
  if not path.exists('./build'):
    mkdir('./build')
  if not 'CLASSPATH' in environ.keys():
    cp_string = '.;%s/antlr4.jar;%%CLASSPATH%%' if platform_os() == 'Windows' else '.:%s/antlr4.jar:%%CLASSPATH%%'
    environ['CLASSPATH'] = cp_string % current_dir
  system('javac -d ./build ./parser/*.java ./base/*.java ./dotnet/*.java')
  chdir('./build')
  system('java Program -f app.txt')
  chdir('..')
