# 📋 Phân tích Terraform: MAX Performance Other Services

## 1️⃣ OVERVIEW

**Mục đích:** Deploy EC2 instance cho các dịch vụ khác (Kafka, Redis) trên AWS nonprod environment (Performance tier)

**Region:** `us-east-1`
**Environment:** `PERFORMANCE` (nonprod)
**Project:** `MAX`

---

## 2️⃣ ARCHITECTURE

```
┌─────────────────────────────────────────────────────────┐
│                    AWS Account (Nonprod)                │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ┌──────────────────────────────────────────────────┐  │
│  │  VPC (172.20.0.0/16)                             │  │
│  │                                                   │  │
│  │  ┌────────────────────────────────────────────┐  │  │
│  │  │  Private Subnet (other-services)           │  │  │
│  │  │                                             │  │  │
│  │  │  ┌─────────────────────────────────────┐   │  │  │
│  │  │  │  EC2 Instance                       │   │  │  │
│  │  │  │  - Type: t3.medium                  │   │  │  │
│  │  │  │  - Name: MAX.Perf.OtherServices     │   │  │  │
│  │  │  │  - Services: Kafka, Redis, SSH      │   │  │  │
│  │  │  │                                     │   │  │  │
│  │  │  │  Ports:                             │   │  │  │
│  │  │  │  - 22 (SSH)                         │   │  │  │
│  │  │  │  - 6379 (Redis)                     │   │  │  │
│  │  │  │  - 9092 (Kafka)                     │   │  │  │
│  │  │  │  - 8091 (Kafka UI)                  │   │  │  │
│  │  │  └─────────────────────────────────────┘   │  │  │
│  │  │                                             │  │  │
│  │  │  ┌──────────────────────────────────────┐   │  │  │
│  │  │  │  Security Group (SG)                 │   │  │  │
│  │  │  │  - Allows CIDR: 172.20.0.0/16        │   │  │  │
│  │  │  │  - Allows CIDR: 10.0.0.0/16          │   │  │  │
│  │  │  │  - All outbound allowed              │   │  │  │
│  │  │  └──────────────────────────────────────┘   │  │  │
│  │  └────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────┘  │
│                                                         │
└─────────────────────────────────────────────────────────┘

Remote State Storage:
┌────────────────────────────────────────┐
│  S3: meperia-edi                       │
│  - Key: terraform/networking/nonprod   │
│  - Region: us-east-1                  │
│  (Networking state - dependency)       │
└────────────────────────────────────────┘
```

---

## 3️⃣ COMPONENTS

### A. Data Source: Remote Networking State
```terraform
data "terraform_remote_state" "networking" {
  backend = "s3"
  config = {
    bucket = "meperia-edi"
    key    = "terraform/networking/nonprod.tfstate"
    region = "us-east-1"
  }
}
```

**Purpose:** Lấy networking info từ remote state (VPC ID, Subnet IDs)

**Output được sử dụng:**
- `vpc_id` - ID của VPC
- `private_app_subnet_ids` - Danh sách các private subnet IDs

---

### B. Local Variables

| Variable | Value | Mục đích |
|----------|-------|---------|
| `vpc_id` | Từ networking state | ID của VPC |
| `private_app_subnet_ids` | Từ networking state | Danh sách private subnets |
| `subnet_id` | `subnet_ids[0]` | Subnet được chọn (first one) |
| `ami_id` | `ami-0360c520857e3138f` | Golden AMI (Kafka/Redis bundle) |
| `allow_cidr_range` | `172.20.0.0/16`, `10.0.0.0/16` | CIDR ranges được phép access |
| `common_tags` | Environment, Project, Owner, etc. | Tags cho tất cả resources |

---

### C. Security Group Rules

**Inbound Rules (cho từng CIDR range `172.20.0.0/16` và `10.0.0.0/16`):**

| Port | Service | Protocol |
|------|---------|----------|
| 22 | SSH | TCP |
| 6379 | Redis | TCP |
| 9092 | Kafka Broker | TCP |
| 8091 | Kafka UI | TCP |

**Outbound Rules:**
- All protocols/ports to `0.0.0.0/0` (Internet)

---

### D. EC2 Instance

**Module:** `../../../../modules/ec2` (custom module)

**Configuration:**
| Parameter | Value |
|-----------|-------|
| Instance Name | `MAX.Perf.OtherServices` |
| Instance Type | `t3.medium` |
| AMI | `ami-0360c520857e3138f` |
| Subnet | Private subnet (first in list) |
| Security Group | `max-perf-other-services-sg` |
| IAM Role | `AmazonSSMRoleForInstancesQuickSetup` |
| Key Pair | `max.dev.key.01` (reused) |

---

## 4️⃣ CÁCH CHẠY (RUN)

### Bước 1: Setup Prerequisites
```bash
# Điều hướng đến thư mục project
cd /home/khoand/Documents/max_devops/infrastructures/max-iac-terraform/live/nonprod/perf/other-services

# Khởi tạo Terraform (download modules, setup backend)
terraform init
```

### Bước 2: Validate Configuration
```bash
# Check syntax và các vấn đề cấu hình
terraform validate

# (Optional) Format code
terraform fmt -recursive
```

### Bước 3: Plan Deployment
```bash
# Xem những thay đổi sẽ được apply
terraform plan -out=tfplan

# Hoặc với file variable custom
terraform plan -var-file="custom.tfvars" -out=tfplan
```

### Bước 4: Apply Changes
```bash
# Apply plan đã tạo
terraform apply tfplan

# Hoặc direct apply (sẽ yêu cầu confirm)
terraform apply
```

### Bước 5: Retrieve Outputs
```bash
# Xem các output values (Instance ID, Private IP, etc.)
terraform output

# Xem output cụ thể
terraform output instance_id
terraform output instance_private_ip
terraform output retrieve_private_key_command
```

### Bước 6: Connect to Instance
```bash
# Retrieve private key từ SSM Parameter Store
aws ssm get-parameter --name [parameter_name] --with-decryption \
  --query 'Parameter.Value' --output text > max.dev.key.01.pem

# Set correct permissions
chmod 600 max.dev.key.01.pem

# SSH connect
ssh -i max.dev.key.01.pem ec2-user@[private_ip]
```

---

## 5️⃣ REQUIREMENTS TRƯỚC KHI CHẠY

### ✅ A. AWS Account & Permissions

**Cần có quyền:**
- `ec2:CreateSecurityGroup`
- `ec2:CreateSecurityGroupRule`
- `ec2:RunInstances`
- `ec2:CreateTags`
- `ec2:DescribeInstances`
- `ec2:DescribeSubnets`
- `ec2:DescribeSecurityGroups`
- `s3:GetObject` (để read remote state)
- `iam:PassRole` (cho IAM role)
- `ssm:GetParameter` (để lấy SSH key nếu được generate)

**Recommend:** Sử dụng IAM role có policy `AdministratorAccess` hoặc tương đương

---

### ✅ B. AWS CLI & Credentials

**Cài đặt:**
```bash
# Install AWS CLI v2
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# Verify
aws --version
```

**Configure Credentials:**
```bash
# Option 1: Interactive configuration
aws configure
# Nhập: AWS Access Key ID, Secret Access Key, Region (us-east-1), Output Format (json)

# Option 2: Environment variables
export AWS_ACCESS_KEY_ID="your-key-id"
export AWS_SECRET_ACCESS_KEY="your-secret-key"
export AWS_DEFAULT_REGION="us-east-1"

# Option 3: AWS SSO
aws sso login --profile max-nonprod
```

**Verify Connection:**
```bash
aws sts get-caller-identity
# Output phải hiển thị AWS Account ID, User/Role ARN
```

---

### ✅ C. Terraform

**Version Requirement:** `>= 1.5.0`

**Install Terraform:**
```bash
# Linux
wget https://releases.hashicorp.com/terraform/1.5.0/terraform_1.5.0_linux_amd64.zip
unzip terraform_1.5.0_linux_amd64.zip
sudo mv terraform /usr/local/bin/

# Verify
terraform version
```

---

### ✅ D. Dependencies - Remote State

**Networking State phải tồn tại:**

```
S3 Bucket: meperia-edi
Path: terraform/networking/nonprod.tfstate
Region: us-east-1

Cần có:
- outputs.vpc_id
- outputs.private_app_subnet_ids
```

**Check:**
```bash
aws s3api head-object --bucket meperia-edi \
  --key terraform/networking/nonprod.tfstate

# Xem content
aws s3 cp s3://meperia-edi/terraform/networking/nonprod.tfstate - | jq '.outputs'
```

---

### ✅ E. EC2 Module

**Module phải tồn tại:**
```
Path: ../../../../modules/ec2
(Relative path trong project structure)

Expected structure:
max-iac-terraform/
├── live/
│   └── nonprod/perf/other-services/
│       └── main.tf (current)
└── modules/
    └── ec2/
        ├── main.tf
        ├── variables.tf
        └── outputs.tf
```

**Verify:**
```bash
ls -la ../../../../modules/ec2/
# Phải có: main.tf, variables.tf, outputs.tf
```

---

### ✅ F. VPC & Subnet Availability

**Cần kiểm tra:**

```bash
# 1. VPC phải tồn tại
aws ec2 describe-vpcs --filters Name=tag:Project,Values=MAX \
  --region us-east-1

# 2. Private subnet phải tồn tại và có sẵn IP
aws ec2 describe-subnets --filters Name=tag:Name,Values=*private-app* \
  --region us-east-1

# 3. AMI phải tồn tại
aws ec2 describe-images --image-ids ami-0360c520857e3138f \
  --region us-east-1
  
# Nếu return empty → AMI không tồn tại, cần update ami_id trong locals
```

---

### ✅ G. IAM Role

**IAM Role `AmazonSSMRoleForInstancesQuickSetup` phải tồn tại:**

```bash
# Verify
aws iam get-role --role-name AmazonSSMRoleForInstancesQuickSetup

# Hoặc list roles
aws iam list-roles | grep -i ssm
```

**Nếu không tồn tại, tạo:**
```bash
# Attach managed policy
aws iam create-instance-profile --instance-profile-name AmazonSSMRoleForInstancesQuickSetup
aws iam create-role --role-name AmazonSSMRoleForInstancesQuickSetup \
  --assume-role-policy-document file://trust-policy.json
aws iam attach-role-policy --role-name AmazonSSMRoleForInstancesQuickSetup \
  --policy-arn arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore
```

---

### ✅ H. Key Pair

**Key Pair `max.dev.key.01` phải tồn tại:**

```bash
# Verify
aws ec2 describe-key-pairs --key-names max.dev.key.01 --region us-east-1

# Nếu không tồn tại, tạo
aws ec2 create-key-pair --key-name max.dev.key.01 --region us-east-1 \
  --query 'KeyMaterial' --output text > max.dev.key.01.pem
chmod 600 max.dev.key.01.pem
```

---

### ✅ I. Backend S3 Bucket

**S3 Bucket `meperia-edi` phải tồn tại:**

```bash
# Verify bucket tồn tại
aws s3api head-bucket --bucket meperia-edi

# Verify có permission
aws s3 ls s3://meperia-edi/

# Check versioning (recommended cho state files)
aws s3api get-bucket-versioning --bucket meperia-edi
```

---

### ✅ J. CIDR Range Access

**Kiểm tra CIDR ranges:**

- `172.20.0.0/16` - Must be valid & reachable
- `10.0.0.0/16` - Must be valid & reachable

**Nếu ranges sai, cần update `allow_cidr_range` trong locals**

---

## 6️⃣ CHECKLIST TRƯỚC KHI CHẠY

```bash
# ✅ 1. Verify AWS Credentials
aws sts get-caller-identity
# Output: Account ID, User ARN

# ✅ 2. Check Terraform Version
terraform version
# Version: >= 1.5.0

# ✅ 3. Verify VPC & Subnets
aws ec2 describe-vpcs --region us-east-1 | jq '.Vpcs[] | {VpcId, CidrBlock}'
aws ec2 describe-subnets --region us-east-1 | jq '.Subnets[] | {SubnetId, CidrBlock, VpcId}'

# ✅ 4. Verify AMI
aws ec2 describe-images --image-ids ami-0360c520857e3138f --region us-east-1

# ✅ 5. Verify IAM Role
aws iam get-role --role-name AmazonSSMRoleForInstancesQuickSetup

# ✅ 6. Verify Key Pair
aws ec2 describe-key-pairs --key-names max.dev.key.01 --region us-east-1

# ✅ 7. Verify S3 Backend
aws s3api head-bucket --bucket meperia-edi
aws s3 cp s3://meperia-edi/terraform/networking/nonprod.tfstate . --dry-run

# ✅ 8. Verify EC2 Module Path
ls -la ../../../../modules/ec2/

# ✅ 9. Validate Terraform Config
terraform validate

# ✅ 10. Generate Plan
terraform plan -out=tfplan

# ✅ 11. Review Plan
terraform show tfplan
```

---

## 7️⃣ POST-DEPLOYMENT

### Outputs to Retrieve:
```bash
# 1. Instance ID
terraform output instance_id

# 2. Private IP Address
terraform output instance_private_ip

# 3. EBS Volume ID
terraform output ebs_volume_id

# 4. SSH Key Name
terraform output effective_key_name

# 5. SSH Key Retrieval Command
terraform output retrieve_private_key_command

# 6. All outputs
terraform output
```

### Verify Instance:
```bash
# 1. Check EC2 Instance
aws ec2 describe-instances --filters Name=tag:Name,Values=MAX.Perf.OtherServices

# 2. Check Security Groups
aws ec2 describe-security-groups --filters Name=group-name,Values=max-perf-other-services-sg

# 3. Check Instance Status
aws ec2 describe-instance-status --instance-ids [instance-id]

# 4. Connect via SSH
ssh -i max.dev.key.01.pem ec2-user@[private-ip]

# 5. Verify Services Running
systemctl status redis-server kafka
```

---

## 8️⃣ CLEANUP (Destroy)

```bash
# Plan destroy
terraform plan -destroy -out=destroy.tfplan

# Review
terraform show destroy.tfplan

# Execute destroy
terraform destroy
# hoặc
terraform apply destroy.tfplan
```

---

## 9️⃣ TROUBLESHOOTING

| Problem | Solution |
|---------|----------|
| `Error: error reading S3 Remote State` | Verify S3 bucket exists & credentials có S3 access |
| `Error: Requested subnet does not exist` | Verify VPC/Subnet từ networking state tồn tại |
| `Error: AMI not found` | Update `ami_id` trong locals với valid AMI |
| `Error: Key pair not found` | Tạo key pair hoặc update `key_name` |
| `Error: IAM role not found` | Tạo IAM role hoặc update `iam_instance_profile` |
| `Backend initialization failed` | Check S3 bucket permissions & network connectivity |

---

**Created:** December 4, 2025
**Last Updated:** December 4, 2025
