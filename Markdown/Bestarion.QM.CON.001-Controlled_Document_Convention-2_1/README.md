---
title: "CONTROLLED DOCUMENT CONVENTION"
subtitle: "QM"
security: "INTERNAL"
doc_code: "Bestarion.QM.CON.001"
updated_by: "Khoa Nguyen-Dinh"
effective_date: "Aug 09, 2023"
version: "2.1"
# QUAN TRỌNG: Dùng 2 dấu gạch chéo \\ để LaTeX nhận được 1 dấu \
template_id: "ODT_Base\\_Template"
header_line: "Bestarion.QM.CON.001-Controlled\\_Document\\_Convention-2\\_1"
toc: false
booktabs: false
use_tables: true
changelog:
  - version: "1.0"
    description: "Khởi tạo tài liệu và phê duyệt lần đầu."
    changed_by: "KhoaND"
    changed_date: "2025-01-01"
    approved: "CEO/TienDD"
    approval_date: "2025-01-05"
  - version: "1.1"
    description: "Cập nhật phần quy trình 3.2."
    changed_by: "MinhNT"
    changed_date: "2025-02-10"
    approved: "N/A"
    approval_date: "N/A"

---

# Introduction
## Purpose

This document explains Terraform module frontend in MAX project, documenting the CloudFront distribution deployment for the PERFORMANCE environment. It provides developers with architecture overview, deployment instructions, and operational guidance to understand, setup, and maintain the infrastructure.

## Scope

  This Terraform module deploys an AWS CloudFront Distribution specifically for the PERFORMANCE environment. It focuses on CloudFront configuration with optimized cache behaviors, custom HTTPS via ACM certificate, and access logging.

  Related dependencies relevant to the configuration are managed externally and out of scope for this document:

  - How S3 bucket is provised and managed about static asset deployment
  - Networking layer (VPC, ALB, subnets).
  - Certificate creation and domain validation.
  - Backend application services behind Load Balancing.

## Definition And Abbreviation

| Term | Definition|
|:----:|:---------:|
|CDN| Content Delivery Network |
|SPA| Single Page Application |
|ALB| Application Load Balancer |
|ACM| AWS Certificate Manager |
|TTL| Time To Live|

: Terms

# Architecture
## Flow

**Request Flow**: Users accessing perf.maxapp.com would hit CloudFront edge locations worldwide.

- Static assets like js and css files are cached up to 24 hours (with `CachingOptimized`) for lightning-fast delivery.
- When user typing `/about` directly or refreshing the page, Cloudfront returns index.html so React router can handle the route.
- API calls with prefix `/api/*` and `/auth/*` will bypass cache completely, forwarding all headers + CORS to ALB backend services.

## Functional

- SPA frontend from S3 (cached static + routing)
- API backend via ALB (no-cache, CORS). 
- Global CDN, custom HTTPS (TLS 1.2+), access logging to S3

# Requirements
## Module & Codes

Depends on : Networking layer remote state

## Version & Provider