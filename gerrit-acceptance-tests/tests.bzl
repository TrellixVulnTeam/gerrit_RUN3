load("//tools/bzl:junit.bzl", "junit_tests")

def acceptance_tests(
    group,
    deps = [],
    labels = [],
    vm_args = ['-Xmx8g'],
    **kwargs):
  junit_tests(
    name = group,
    deps = deps + [
      '//gerrit-acceptance-tests:lib',
      "//lib/bouncycastle:bcpkix",
      "//lib/bouncycastle:bcpg",
    ],
    tags = labels + [
      'acceptance',
      'slow',
    ],
    size = "large",
    jvm_flags = vm_args,
    **kwargs
  )
