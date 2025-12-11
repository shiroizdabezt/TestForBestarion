# Requirement
- data "terraform_remote_state" "networking": Synchronize state information with other components of the system.  
- allow_cidr_range:  
    - "172.20.0.0/16": CIDR block of the current VPC hosting these resources.
    - "10.0.0.0/16": CIDR block of the MSS project VPC within the same AWS cloud environment.  
- common_tags: Define tags for easier management and governance.    
- ami_id: Since there is no specific Linux version requirement, the latest Ubuntu AMI will be used.  
- data "aws_subnet" "private_app_a": Retrieve a private subnet using the VPC ID obtained from S3 (remote state). Filters applied:  
    - vpc_id = local.vpc_id: VPC from networking state.
    - name = "tag:Name": Search based on the Subnet's "Name" tag in AWS.  
    - values = ["*private-app-a"]: Find subnets with names ending in "private-app-a".  
- resource "aws_security_group" "other_services":  
    - Declare the name, description, and the associated VPC.  
    - Egress: Allow unrestricted outbound access to the internet (allow all).  
    - Apply both project-wide common tags and service-specific tags.  
- resource "aws_security_group_rule" "allow_ssh": Allow SSH access from the CIDR ranges defined above.  
- resource "aws_security_group_rule" "allow_redis": Open the port for Redis access from the defined CIDR ranges.  
- resource "aws_security_group_rule" "allow_kafka": Open the port for Kafka access from the defined CIDR ranges.  
- resource "aws_security_group_rule" "allow_kafka_ui": Open the port for Kafka UI access from the defined CIDR ranges.  
- module "ec2":  
    - source: Utilize the pre-defined module.  
    - instance_name: Assign a name to this EC2 instance.
    - instance_type: Select an instance type with resources appropriate for its workload.  
    - ami_id: Use the AMI defined above.  
    - subnet_id: Use private_app_a subnet from aws_subnet filter instead of subnet list.  
    - security_group_ids: Attach the Security Group defined above to this EC2.  
    - iam_instance_profile: Attach a role enabling communication with AWS Systems Manager (SSM). This allows remote access via the AWS Console, automated   patching, and remote command execution.  
    - ensure_key_pair: Specify the requirement of a key pair for EC2 login.  
    - key_name: The specific key pair to be used.   
    - tags: Assign tags to this instance.  


# Purpose
Deploy EC2 instance with Kafka, Redis, and related services in AWS nonprod testing environment with proper networking, security, and access controls.

# Prerequisites

Before running the Terraform configuration, verify the following prerequisites are met:

## 1. AWS Account & Credentials
**Required IAM Permissions:**
- `ec2:CreateSecurityGroup`
- `ec2:CreateSecurityGroupRule`
- `ec2:RunInstances`
- `ec2:CreateTags`
- `ec2:DescribeSubnets`
- `ec2:DescribeSecurityGroups`
- `ec2:DescribeInstances`
- `s3:GetObject`
- `iam:PassRole`

## 2. S3 Backend & Networking State
```bash
# Verify S3 bucket exists
aws s3api head-bucket --bucket meperia-edi

# Verify networking state file exists
aws s3 ls s3://meperia-edi/terraform/networking/nonprod.tfstate

# View VPC and subnet IDs from networking state
aws s3 cp s3://meperia-edi/terraform/networking/nonprod.tfstate - | jq '.outputs | {vpc_id, private_app_subnet_ids}'
```

**What you should see:**
- `vpc_id`: VPC ID (e.g., vpc-xxxxxxxx)
- `private_app_subnet_ids`: List of private subnet IDs

## 3. VPC & Subnets Exist
```bash
# Get VPC details (should match vpc_id from networking state)
aws ec2 describe-vpcs --filters Name=cidr,Values=172.20.0.0/16 --region us-east-1

# Verify private subnets exist
aws ec2 describe-subnets --filters Name=vpc-id,Values=vpc-XXXXXXXX --region us-east-1 \
  --query 'Subnets[?MapPublicIpOnLaunch==`false`].{SubnetId:SubnetId,CidrBlock:CidrBlock}'
```

## 4. AMI Validation
```bash
# Verify the golden AMI exists (with Kafka/Redis bundle)
aws ec2 describe-images --image-ids ami-0360c520857e3138f --region us-east-1
```

## 5. IAM Role Exists
```bash
# Check if AmazonSSMRoleForInstancesQuickSetup role exists
aws iam get-role --role-name AmazonSSMRoleForInstancesQuickSetup
```

## 6. EC2 Key Pair Exists
```bash
# Verify key pair exists
aws ec2 describe-key-pairs --key-names max.dev.key.01 --region us-east-1
```

## 7. EC2 Module Available
```bash
# Verify EC2 module exists in project structure
ls -la /nonprod/testing/../../modules/ec2/

# Should contain: main.tf, variables.tf, outputs.tf, outputs.tf
```

## 9. Networking CIDR Ranges Valid
```bash
# Verify CIDR ranges are reachable and valid:
# - 172.20.0.0/16 (Current VPC)
# - 10.0.0.0/16 (MSS project VPC)

aws ec2 describe-vpcs --region us-east-1 --query 'Vpcs[].{VpcId:VpcId,CidrBlock:CidrBlock}' --output table
```

## 10. Network Connectivity
```bash
# Ensure you can reach AWS from your machine
ping 8.8.8.8

# Ensure AWS CLI can reach AWS APIs (should return 200)
curl -I https://sts.amazonaws.com/
```

---

# How to Run Terraform

## Step-by-Step Deployment Instructions

### Step 1: Initialize Terraform
```bash
cd /nonprod/testing/other-services
terraform init
```

### Step 2: Validate Configuration
```bash
terraform validate

# Expected output:
# Success! The configuration is valid.
```

### Step 3: Format Code (Optional)
```bash
# Format Terraform code for consistency
terraform fmt -recursive
```

### Step 4: Generate Execution Plan
```bash
terraform plan -out=tfplan

# Expected output:
# Plan: X to add, 0 to change, 0 to destroy.

# Review the plan details:
terraform show tfplan
```

### Step 5: Apply Configuration
```bash
terraform apply tfplan

# Expected output:
# Apply complete! Resources: 9 added, 0 changed, 0 destroyed.
```

### Step 6: Retrieve Outputs
```bash
terraform output
```

### Step 7: Retrieve SSH Private Key (if needed)
```bash
RETRIEVE_CMD=$(terraform output -raw retrieve_private_key_command)
echo $RETRIEVE_CMD

# Execute the command to get private key
eval $RETRIEVE_CMD
```

### Step 8: Connect to EC2 Instance
```bash
INSTANCE_IP=$(terraform output -raw instance_private_ip)

# SSH into the instance
ssh -i max.dev.key.01.pem ec2-user@$INSTANCE_IP
```


## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | >= 1.5.0 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | >= 5.0 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | 6.22.1 |
| <a name="provider_terraform"></a> [terraform](#provider\_terraform) | n/a |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_ec2"></a> [ec2](#module\_ec2) | ../../../../modules/ec2 | n/a |

## Resources

| Name | Type |
|------|------|
| [aws_security_group.other_services](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group) | resource |
| [aws_security_group_rule.allow_kafka](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule) | resource |
| [aws_security_group_rule.allow_kafka_ui](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule) | resource |
| [aws_security_group_rule.allow_redis](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule) | resource |
| [aws_security_group_rule.allow_ssh](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule) | resource |
| [aws_subnet.private_app_a](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/subnet) | data source |
| [terraform_remote_state.networking](https://registry.terraform.io/providers/hashicorp/terraform/latest/docs/data-sources/remote_state) | data source |

## Inputs

No inputs.

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_ebs_volume_id"></a> [ebs\_volume\_id](#output\_ebs\_volume\_id) | ID of the attached EBS volume |
| <a name="output_effective_key_name"></a> [effective\_key\_name](#output\_effective\_key\_name) | The key name associated with the EC2 instance |
| <a name="output_instance_id"></a> [instance\_id](#output\_instance\_id) | ID of the created EC2 instance |
| <a name="output_instance_private_ip"></a> [instance\_private\_ip](#output\_instance\_private\_ip) | Private IP of the EC2 instance |
| <a name="output_private_key_parameter_name"></a> [private\_key\_parameter\_name](#output\_private\_key\_parameter\_name) | SSM Parameter name storing the generated private key (if generated) |
| <a name="output_retrieve_private_key_command"></a> [retrieve\_private\_key\_command](#output\_retrieve\_private\_key\_command) | AWS CLI command to retrieve the private key from SSM Parameter Store |


## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_az_ids"></a> [az\_ids](#input\_az\_ids) | Optional list of availability\_zone\_ids to pin subnets (e.g., ["use1-az1", "use1-az2"]). | `list(string)` | `null` | no |
| <a name="input_endpoints"></a> [endpoints](#input\_endpoints) | Toggle creation of VPC endpoints. Gateway: s3. Interface: ecr.api, ecr.dkr, logs, monitoring, secretsmanager, ssm, ec2messages, ssmmessages, sts. | <pre>object({<br/>    s3 = bool<br/>    # ecr_api        = bool<br/>    # ecr_dkr        = bool<br/>    # logs           = bool<br/>    # monitoring     = bool<br/>    # secretsmanager = bool<br/>    # ssm            = bool<br/>    # ec2messages    = bool<br/>    # ssmmessages    = bool<br/>    # sts            = bool<br/>  })</pre> | n/a | yes |
| <a name="input_environment"></a> [environment](#input\_environment) | Environment tag value. | `string` | n/a | yes |
| <a name="input_managed_by"></a> [managed\_by](#input\_managed\_by) | ManagedBy tag value. | `string` | `"Terraform"` | no |
| <a name="input_name"></a> [name](#input\_name) | Logical name of the VPC (e.g., MAX). | `string` | n/a | yes |
| <a name="input_nat_mode"></a> [nat\_mode](#input\_nat\_mode) | NAT strategy: 'single' for one NAT GW; 'ha' for one per AZ. | `string` | n/a | yes |
| <a name="input_owner"></a> [owner](#input\_owner) | Owner tag value. | `string` | n/a | yes |
| <a name="input_project"></a> [project](#input\_project) | Project tag value. | `string` | n/a | yes |
| <a name="input_subnet_cidrs"></a> [subnet\_cidrs](#input\_subnet\_cidrs) | /20 blocks for each subnet group per AZ. Supports A/B and optional C. | <pre>object({<br/>    public_a      = string<br/>    private_app_a = string<br/>    private_db_a  = string<br/>    public_b      = string<br/>    private_app_b = string<br/>    private_db_b  = string<br/>    public_c      = string<br/>    private_app_c = string<br/>    private_db_c  = string<br/>  })</pre> | n/a | yes |
| <a name="input_vpc_cidr"></a> [vpc\_cidr](#input\_vpc\_cidr) | CIDR for the VPC (e.g., 172.20.0.0/16). | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_az_ids_selected"></a> [az\_ids\_selected](#output\_az\_ids\_selected) | n/a |
| <a name="output_nat_gateway_id"></a> [nat\_gateway\_id](#output\_nat\_gateway\_id) | NAT Gateway ID (null if nat\_mode != single) |
| <a name="output_private_app_subnet_ids"></a> [private\_app\_subnet\_ids](#output\_private\_app\_subnet\_ids) | n/a |
| <a name="output_private_db_subnet_ids"></a> [private\_db\_subnet\_ids](#output\_private\_db\_subnet\_ids) | n/a |
| <a name="output_public_subnet_ids"></a> [public\_subnet\_ids](#output\_public\_subnet\_ids) | n/a |
| <a name="output_route_table_ids"></a> [route\_table\_ids](#output\_route\_table\_ids) | n/a |
| <a name="output_vpc_endpoint_ids"></a> [vpc\_endpoint\_ids](#output\_vpc\_endpoint\_ids) | Map of endpoint IDs (may be null if disabled) |
| <a name="output_vpc_id"></a> [vpc\_id](#output\_vpc\_id) | n/a |