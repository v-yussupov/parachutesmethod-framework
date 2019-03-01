{
  "AWSTemplateFormatVersion": "2010-09-09",
  "Description": "Deploys functionality as serverless parachutes and an nginx server to load balance requests between the original application and its parachutes.",
  "Parameters": {
    "InstanceType": {
      "Type": "String",
      "Default": "t2.micro",
      "AllowedValues": [
        "t2.micro",
        "t2.small",
        "t2.medium",
        "t2.large",
        "m4.large",
        "c4.large"
      ],
      "Description": "EC2 instance type (e.g., t2.micro, t2.small, t2.medium, m4.large, c4.large)"
    }
  },
  "Mappings": {
    "AWSInstanceType2Arch": {
      "t2.micro": {
        "Arch": "HVM64"
      },
      "t2.small": {
        "Arch": "HVM64"
      },
      "t2.medium": {
        "Arch": "HVM64"
      },
      "t2.large": {
        "Arch": "HVM64"
      },
      "m4.large": {
        "Arch": "HVM64"
      },
      "c4.large": {
        "Arch": "HVM64"
      }
    },
    "AWSRegionArch2AMI": {
      "us-east-1": {
        "HVM64": "ami-7f6aa912"
      },
      "us-west-2": {
        "HVM64": "ami-f469ad94"
      },
      "us-west-1": {
        "HVM64": "ami-594b0f39"
      },
      "eu-west-1": {
        "HVM64": "ami-c5da42b6"
      },
      "eu-west-2": {
        "HVM64": "ami-896369ed"
      },
      "eu-west-3": {
        "HVM64": "NOT_SUPPORTED"
      },
      "eu-central-1": {
        "HVM64": "ami-e526ce8a"
      },
      "ap-northeast-1": {
        "HVM64": "ami-7f4db91e"
      },
      "ap-northeast-2": {
        "HVM64": "ami-39448f57"
      },
      "ap-northeast-3": {
        "HVM64": "NOT_SUPPORTED"
      },
      "ap-southeast-1": {
        "HVM64": "ami-a69b49c5"
      },
      "ap-southeast-2": {
        "HVM64": "ami-10361e73"
      },
      "ap-south-1": {
        "HVM64": "ami-fdbed492"
      },
      "us-east-2": {
        "HVM64": "ami-d90c57bc"
      },
      "ca-central-1": {
        "HVM64": "ami-33f34157"
      },
      "sa-east-1": {
        "HVM64": "ami-aed144c2"
      },
      "cn-north-1": {
        "HVM64": "ami-14519b79"
      },
      "cn-northwest-1": {
        "HVM64": "ami-ec868c8e"
      }
    },
    "Region2Principal": {
      "us-east-1": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      },
      "us-west-2": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      },
      "us-west-1": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      },
      "eu-west-1": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      },
      "ap-southeast-1": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      },
      "ap-northeast-1": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      },
      "ap-southeast-2": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      },
      "ap-northeast-2": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      },
      "ap-northeast-3": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      },
      "ap-south-1": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      },
      "us-east-2": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      },
      "sa-east-1": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      },
      "cn-north-1": {
        "EC2Principal": "ec2.amazonaws.com.cn",
        "OpsWorksPrincipal": "opsworks.amazonaws.com.cn"
      },
      "cn-northwest-1": {
        "EC2Principal": "ec2.amazonaws.com.cn",
        "OpsWorksPrincipal": "opsworks.amazonaws.com.cn"
      },
      "eu-central-1": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      },
      "eu-west-2": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      },
      "ca-central-1": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      },
      "eu-west-3": {
        "EC2Principal": "ec2.amazonaws.com",
        "OpsWorksPrincipal": "opsworks.amazonaws.com"
      }
    }
  },
  "Resources": {
    "CFNRole": {
      "Type": "AWS::IAM::Role",
      "Properties": {
        "AssumeRolePolicyDocument": {
          "Statement": [
            {
              "Effect": "Allow",
              "Principal": {
                "Service": {
                  "Fn::FindInMap": [
                    "Region2Principal",
                    {
                      "Ref": "AWS::Region"
                    },
                    "EC2Principal"
                  ]
                }
              },
              "Action": [
                "sts:AssumeRole"
              ]
            }
          ]
        },
        "Path": "/"
      }
    },
    "CFNRolePolicy": {
      "Type": "AWS::IAM::Policy",
      "Properties": {
        "PolicyName": "CloudFormerPolicy",
        "PolicyDocument": {
          "Statement": [
            {
              "Effect": "Allow",
              "Action": [
                "cloudformation:Describe*",
                "ec2:Describe*"
              ],
              "Resource": "*"
            }
          ]
        },
        "Roles": [
          {
            "Ref": "CFNRole"
          }
        ]
      }
    },
    "CFNInstanceProfile": {
      "Type": "AWS::IAM::InstanceProfile",
      "Properties": {
        "Path": "/",
        "Roles": [
          {
            "Ref": "CFNRole"
          }
        ]
      }
    },
    "NginxInstance": {
      "Type": "AWS::EC2::Instance",
      "DependsOn": "RouteVPCAny",
      "CreationPolicy": {
        "ResourceSignal": {
          "Timeout": "PT30M"
        }
      },
      "Metadata": {
        "AWS::CloudFormation::Init": {
          "base": {
            "packages": {
              "yum": {
                "autoconf": [],
                "automake": [],
                "bison": [],
                "bzip2": [],
                "gcc": [],
                "gcc-c++": [],
                "libffi-devel": [],
                "libtool": [],
                "libxml2-devel": [],
                "libxslt-devel": [],
                "libyaml-devel": [],
                "make": [],
                "openssl-devel": [],
                "patch": [],
                "readline": [],
                "readline-devel": [],
                "ruby-devel": [],
                "sqlite-devel": [],
                "zlib": [],
                "zlib-devel": []
              }
            }
          },
          "nginx": {
            "commands": {
              "01_install_nginx": {
                "command": "amazon-linux-extras install nginx1.12"
              }
            },
            "files": {
<#list configs as parachuteName, routerConf>
              "/etc/nginx/conf.d/${parachuteName}.conf": {
                "content": {
                  "Fn::Join": [
                    "",
                    [
                      ${routerConf}
                    ]
                  ]
                },
                "group": "root",
                "mode": "000400",
                "owner": "root"
              }<#sep>, </#sep>
</#list>

            }
          },
          "configSets": {
            "full_install": [
              "base",
              "nginx",
              ""
            ]
          }
        }
      },
      "Properties": {
        "IamInstanceProfile": {
          "Ref": "CFNInstanceProfile"
        },
        "ImageId": {
          "Fn::FindInMap": [
            "AWSRegionArch2AMI",
            {
              "Ref": "AWS::Region"
            },
            {
              "Fn::FindInMap": [
                "AWSInstanceType2Arch",
                {
                  "Ref": "InstanceType"
                },
                "Arch"
              ]
            }
          ]
        },
        "InstanceType": {
          "Ref": "InstanceType"
        },
        "SubnetId": {
          "Ref": "VPCSubnet"
        },
        "SecurityGroupIds": [
          {
            "Ref": "NginxSecurityGroup"
          }
        ],
        "UserData": {
          "Fn::Base64": {
            "Fn::Join": [
              "",
              [
                "#!/bin/bash -xe\n",
                "yum update -y aws-cfn-bootstrap\n",
                "/opt/aws/bin/cfn-init -v ",
                "         --stack ",
                {
                  "Ref": "AWS::StackId"
                },
                "         --resource NginxVPC ",
                "         --configsets full_install ",
                "         --region ",
                {
                  "Ref": "AWS::Region"
                },
                "\n",
                "/opt/aws/bin/cfn-signal -e $? ",
                "         --stack ",
                {
                  "Ref": "AWS::StackId"
                },
                "         --resource NginxVPC ",
                "         --region ",
                {
                  "Ref": "AWS::Region"
                },
                "\n"
              ]
            ]
          }
        }
      }
    },
    "NginxSecurityGroup": {
      "Type": "AWS::EC2::SecurityGroup",
      "Properties": {
        "GroupDescription": "Enable HTTP access via port 80",
        "VpcId": {
          "Ref": "VPC"
        },
        "SecurityGroupIngress": [
          {
            "IpProtocol": "tcp",
            "FromPort": "80",
            "ToPort": "80",
            "CidrIp": "0.0.0.0/0"
          }
        ]
      }
    },
    "VPC": {
      "Type": "AWS::EC2::VPC",
      "Properties": {
        "Tags": [
          {
            "Key": "Name",
            "Value": "NginxVPC"
          }
        ],
        "CidrBlock": "10.10.10.0/24",
        "EnableDnsSupport": "true",
        "EnableDnsHostnames": "true"
      }
    },
    "VPCSubnet": {
      "Type": "AWS::EC2::Subnet",
      "Properties": {
        "MapPublicIpOnLaunch": "true",
        "AvailabilityZone": {
          "Fn::Select": [
            "0",
            {
              "Fn::GetAZs": {
                "Ref": "AWS::Region"
              }
            }
          ]
        },
        "VpcId": {
          "Ref": "VPC"
        },
        "CidrBlock": "10.10.10.0/24"
      }
    },
    "VPCInternetGateway": {
      "Type": "AWS::EC2::InternetGateway",
      "Properties": {}
    },
    "VPCAttachGateway": {
      "Type": "AWS::EC2::VPCGatewayAttachment",
      "Properties": {
        "VpcId": {
          "Ref": "VPC"
        },
        "InternetGatewayId": {
          "Ref": "VPCInternetGateway"
        }
      }
    },
    "VPCRouteTable": {
      "Type": "AWS::EC2::RouteTable",
      "Properties": {
        "Tags": [
          {
            "Key": "Name",
            "Value": "NginxVPCRouteTable"
          }
        ],
        "VpcId": {
          "Ref": "VPC"
        }
      }
    },
    "VPCSubnetRouteTableAssociation": {
      "Type": "AWS::EC2::SubnetRouteTableAssociation",
      "Properties": {
        "SubnetId": {
          "Ref": "VPCSubnet"
        },
        "RouteTableId": {
          "Ref": "VPCRouteTable"
        }
      }
    },
    "RouteVPCAny": {
      "Type": "AWS::EC2::Route",
      "DependsOn": "VPCAttachGateway",
      "Properties": {
        "RouteTableId": {
          "Ref": "VPCRouteTable"
        },
        "DestinationCidrBlock": "0.0.0.0/0",
        "GatewayId": {
          "Ref": "VPCInternetGateway"
        }
      }
    }
  },
  "Outputs": {
    "CustomVPCWebsiteURL": {
      "Description": "URL for Nginx",
      "Value": {
        "Fn::Join": [
          "",
          [
            "http://",
            {
              "Fn::GetAtt": [
                "NginxVPC",
                "PublicDnsName"
              ]
            }
          ]
        ]
      }
    }
  }
}