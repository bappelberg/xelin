output "instance_public_ip" {
  description = "Public IP adress for the VM"
  value       = oci_core_instance.app.public_ip
}

output "instance_id" {
  description = "OCID for VM instance"
  value       = oci_core_instance.app.id
}

output "ssh_command" {
  description = "SSH commando for connecting to VM"
  value       = "ssh ubuntu@${oci_core_instance.app.public_ip}"
}
