variable "tenancy_ocid" {
  description = "OCID för din OCI-tenant"
  type        = string
}

variable "user_ocid" {
  description = "OCID för din OCI-användare"
  type        = string
}

variable "fingerprint" {
  description = "Fingerprint för din API-nyckel"
  type        = string
}

variable "private_key_path" {
  description = "Sökväg till din OCI API-privata nyckel (.pem)"
  type        = string
}

variable "region" {
  description = "OCI region, t.ex. eu-stockholm-1"
  type        = string
  default     = "eu-stockholm-1"
}

variable "compartment_ocid" {
  description = "OCID för compartment där resurser skapas (använd root-compartment-OCID = tenancy_ocid för free tier)"
  type        = string
}

variable "availability_domain" {
  description = "Availability domain, t.ex. Uocm:EU-STOCKHOLM-1-AD-1"
  type        = string
}

variable "project_name" {
  description = "Namn på projektet, används som prefix i resursnamn"
  type        = string
  default     = "xelin"
}

variable "environment" {
  description = "Miljö: dev, staging eller prod"
  type        = string
  default     = "prod"
}

variable "ssh_public_key" {
  description = "SSH public key för VM-access. Lämna tomt för att auto-generera ett nyckelpar."
  type        = string
  default     = ""
  sensitive   = true
}
