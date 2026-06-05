#!/bin/bash
set -e

DOMAIN="xelin.duckdns.org"
EMAIL="benjamin.w.appelberg@gmail.com"
COMPOSE="docker compose -f docker-compose.yml -f docker-compose.prod.yml"

# Step 1: Create a dummy self-signed cert inside the letsencrypt Docker volume
# so nginx can start (nginx refuses to start if the cert path doesn't exist)
echo "### Creating temporary self-signed cert..."
$COMPOSE run --rm --entrypoint "" certbot sh -c "
  mkdir -p /etc/letsencrypt/live/$DOMAIN
  openssl req -x509 -nodes -newkey rsa:2048 -days 1 \
    -keyout /etc/letsencrypt/live/$DOMAIN/privkey.pem \
    -out    /etc/letsencrypt/live/$DOMAIN/fullchain.pem \
    -subj '/CN=$DOMAIN'
"

# Step 2: Start nginx with the dummy cert (nginx will now start successfully)
echo "### Starting nginx..."
$COMPOSE up -d nginx
sleep 3

# Step 3: Request a real certificate via the ACME HTTP challenge
# Certbot writes a challenge file to /var/www/certbot, nginx serves it over port 80
# Let's Encrypt fetches it to verify we own the domain, then issues the cert
echo "### Requesting Let's Encrypt certificate..."
$COMPOSE run --rm certbot certonly \
  --webroot \
  --webroot-path=/var/www/certbot \
  --email "$EMAIL" \
  --agree-tos \
  --no-eff-email \
  -d "$DOMAIN"

# Step 4: Reload nginx so it picks up the real cert
echo "### Reloading nginx with the real certificate..."
$COMPOSE exec nginx nginx -s reload

# Step 5: Start remaining services
echo "### Starting all services..."
$COMPOSE up -d

echo ""
echo "Done! Certificate is live. Certbot renews automatically every 12 hours."
