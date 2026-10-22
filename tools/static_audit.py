#!/usr/bin/env python3
"""Dependency-free, limited source-contract checks. Not a compiler or runtime test."""
from pathlib import Path
import re,sys,xml.etree.ElementTree as ET
root=Path(__file__).resolve().parents[1]
checks=[]
def check(name,condition):
    checks.append((name,bool(condition)))
    print(("PASS" if condition else "FAIL")+": "+name)
backend=root/'backend'; front=root/'frontend'
pom=(backend/'pom.xml').read_text(encoding='utf-8')
try: ET.fromstring(pom); check('pom.xml parses as XML',True)
except ET.ParseError:check('pom.xml parses as XML',False)
policy=(backend/'src/main/java/org/learnsphere/security/DemoAccessPolicy.java').read_text()
check('default deny rule present','.anyRequest().denyAll()' in policy)
check('no unrestricted lesson subtree matcher','"/api/v1/lessons/**"' not in policy)
check('specific lesson assessment permitted','"/api/v1/lessons/*/assessment"' in policy)
check('demo submissions permitted only via POST','requestMatchers(HttpMethod.POST, "/api/v1/assessments/*/submissions")' in policy)
check('synthetic data warning in policy','Synthetic-data-only' in policy)
check('database migration present',(backend/'src/main/resources/db/migration/V1__create_demo_attempt_tables.sql').exists())
check('backend security integration tests present',(backend/'src/test/java/org/learnsphere/api/AccessBoundaryIntegrationTest.java').exists())
check('frontend package metadata present',(front/'package.json').exists())
check('Vite backend proxy configured',"127.0.0.1:8080" in (front/'vite.config.ts').read_text())
check('Spring binds to loopback by default','127.0.0.1' in (backend/'src/main/resources/application.properties').read_text())
check('test datasource configured',(backend/'src/test/resources/application.properties').exists())
print(f"\nStatic checks: {sum(x for _,x in checks)}/{len(checks)} passed")
print('NOTE: This does not execute Maven, JUnit, React, Vite, or database tests.')
sys.exit(0 if all(x for _,x in checks) else 1)
