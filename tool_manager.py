
from os import system, chdir, getcwd, path
from urllib.request import urlopen


current_dir = getcwd()


def antlr_exists():
  return path.exists('./antlr4.jar')


def download_antlr():
  url = 'https://www.antlr.org/download/antlr-4.9.2-complete.jar'
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

