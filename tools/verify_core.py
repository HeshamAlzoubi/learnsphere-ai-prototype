#!/usr/bin/env python3
"""Compile and execute the dependency-free Java scoring tests using JDK 21."""
from pathlib import Path
import subprocess, tempfile, sys
root=Path(__file__).resolve().parents[1]
core=root/'backend/src/main/java/org/learnsphere/core'
test=root/'backend/src/test/java/org/learnsphere/core/PolicySelfTest.java'
files=sorted(core.glob('*.java'))+[test]
with tempfile.TemporaryDirectory(prefix='learnsphere-core-') as tmp:
    for args in [['javac','-d',tmp,*map(str,files)],['java','-cp',tmp,'org.learnsphere.core.PolicySelfTest']]:
        print('Running:',args[0],flush=True)
        r=subprocess.run(args,check=False)
        if r.returncode: sys.exit(r.returncode)
