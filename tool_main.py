
from tool_manager import antlr_exists, download_antlr, run_antlr
from tool_generators import generator_asp


def show_menu():
  options = [
    '0. Close the program',
    '1. Generate the parser',
    '2. Generate a Java based back end (coming soon)',
    '3. Generate a C# based back end',
    '4. Generate a Go backend (planned feature)',
    '5. Generate Typescript front end support code'
  ]
  print('Select an option')
  [print(option) for option in options]
  choice = input()
  try:
    return int(choice)
  except:
    return -1


def get_grammer_file_name():
  print('Enter the grammar file name (default: Generator)')
  file_name = input()
  if len(file_name) == 0:
    file_name = 'Generator'
  return file_name


is_running = True

if not antlr_exists():
  download_antlr()

while is_running:
  sm = show_menu()
  if sm == 0:
    is_running = False
  elif sm == 1:
    run_antlr(get_grammer_file_name())
  elif sm == 2:
    print('coming soon')
  elif sm == 3:
    generator_asp()
  elif sm == 4:
    print('planned feature')
  elif sm == 5:
    print('coming soon')
  else:
    continue


