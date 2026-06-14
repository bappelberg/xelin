#!/bin/bash

sleep 5

echo "=== Kontrollerar om LDAP redan har data ==="
EXISTING=$(ldapsearch -x -H ldap://localhost -b "dc=foi,dc=se" -D "cn=admin,dc=foi,dc=se" -w admin 2>/dev/null | grep -c "uid=")

if [ "$EXISTING" -eq "0" ]; then
    echo "=== Importerar LDIF-data ==="
    ldapadd -x -H ldap://localhost -D "cn=admin,dc=foi,dc=se" -w admin -f /tmp/bootstrap.ldif
    echo "=== Import klar! ==="
else
    echo "=== Data finns redan ($EXISTING användare), hoppar över import ==="
fi
