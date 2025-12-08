# Requirement
- data "terraform_remote_state" "networking": Synchronize state information with other components of the system.  
- allow_cidr_range:  
    - "172.20.0.0/16": CIDR block of the current VPC hosting these resources.
    - "10.0.0.0/16": CIDR block of the MSS project VPC within the same AWS cloud environment.  
- common_tags: Define tags for easier management and governance.    
- ami_id: Since there is no specific Linux version requirement, the latest Ubuntu AMI will be used.  
- data "aws_subnet" "private_app_a": Retrieve a private subnet using the VPC ID obtained from S3 (remote state). Filters applied:  
    - name = "tag:Name": Search based on the Subnet's "Name" tag in AWS.  
    - values = ["*prod-private-app-a"]: Find subnets with names ending in "prod-private-app-a".  
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
    - subnet_id: Place this EC2 into the correct private subnet defined above.  
    - security_group_ids: Attach the Security Group defined above to this EC2.  
    - iam_instance_profile: Attach a role enabling communication with AWS Systems Manager (SSM). This allows remote access via the AWS Console, automated   patching, and remote command execution.  
    - ensure_key_pair: Specify the requirement of a key pair for EC2 login.  
    - key_name: The specific key pair to be used.   
    - tags: Assign tags to this instance.  


# Purpose
Deploy EC2 instance with Kafka, Redis, and related services in AWS nonprod performance environment with proper networking, security, and access controls.

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
ls -la /nonprod/perf/../../modules/ec2/

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
cd /nonprod/perf/other-services
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

