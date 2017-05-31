#include "CoreDumper.h"

void bar() {
  // the following statements will produce a core
  int* p = NULL;
  *p = 5;
 
  // alternatively:
  // abort();
}
 
void foo() {
  bar();
}
 
JNIEXPORT void JNICALL Java_CoreDumper_core
  (JNIEnv *env, jobject obj) {
  foo();
}
