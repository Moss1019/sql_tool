
import os

from antlr_setup import download_antlr_jar, run_antlr, compile_java, run_java

default_antlr_file = 'Definition'
default_file_name = 'test.txt'
default_package_name = 'com.example'
default_db_user = 'default_user'
default_backend_folder = './backend'
default_frontend_folder = './frontend'
default_end_point = 'http://localhost:8080'

def get_choice():
  print('Select an option to run')
  print('0. exit')
  print('1. download antlr4 jar file')
  print('2. run antlr on a specified g4 grammar file')
  print('3. compile all java file in the root and ./parser directories')
  print('4. generate spring boot with mysql support')
  print('5. generate spring boot with firestore support')
  print('6. generate spring boot with in memory data storage')
  choice = -1
  try:
    choice = int(input())
  except:
    pass
  return choice


def get_args():
  args = [default_package_name, default_db_user, default_backend_folder, default_frontend_folder, default_end_point]
  if not os.path.exists('./config.props'):
    print('config.props not found')
    return args
  f = open('./config.props', 'rt')
  for line in f.readlines():
    parts = line.split(' ')
    if parts[0] == '-pack':
      args[0] = parts[1].strip()
    if parts[0] == '-dbuser':
      args[1] = parts[1].strip()
    if parts[0] == '-bend':
      args[2] = parts[1].strip()
    if parts[0] == '-fend':
      args[3] = parts[1].strip()
    if parts[0] == '-endp':
      args[4] = parts[1].strip()
  f.close()
  return args


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
      antlr_file = default_antlr_file
    print('running antlr...')
    run_antlr(antlr_file)
  elif choice == 3:
    print('compiling antlr and application .java files...')
    compile_java()
  elif choice >= 4:
    data_option = choice - 4
    file_name = input('enter the name of the file containing the definition\n => ')
    if len(file_name) == 0:
      file_name = default_file_name
    print('running java language app...')
    package_name, db_user, be, fe, endp = get_args()
    run_java(data_option, file_name, package_name, db_user, be, fe, endp)
  else:
    continue
