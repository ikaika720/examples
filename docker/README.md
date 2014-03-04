Docker examples
===============
## pipework memo
See [docker configure networking](http://docs.docker.io/en/latest/use/networking/) and [pipework](https://github.com/jpetazzo/pipework).

Vagrantfile

    Vagrant.configure("2") do |config|
      config.vm.network :private_network, ip: "192.168.33.20", adapter: 2
      config.vm.provider :virtualbox do |vb, override|
        vb.customize ["modifyvm", :id, "--nicpromisc2", "allow-all"]
        vb.customize ["modifyvm", :id, "--nictype2", "Am79C973"]
      end
    end

See [VirtualBox Configuration](http://docs.vagrantup.com/v2/virtualbox/configuration.html) and [VBoxManage](http://www.virtualbox.org/manual/ch08.html) about Vagrant and VirtualBox configuration.

Docker VM

    sudo apt-get -y install git iputils-arping
    git clone https://github.com/jpetazzo/pipework.git
    sudo ip addr flush dev eth1
    sudo brctl addbr br1
    sudo brctl addif br1 eth1
    sudo ip addr add 192.168.1.254/24 dev br1
    sudo ip link set dev br1 up
    sudo pipework/pipework br1 <container_id> 192.168.1.1/24

See [NetworkConnectionBridge](https://help.ubuntu.com/community/NetworkConnectionBridge) about network bridge on Ubuntu.

If you faced the VirtualBox VM starts too slow due to the boot time network configuration, see [`vagrant up` hangs at "Waiting for VM to boot. This can take a few minutes"](https://github.com/mitchellh/vagrant/wiki/%60vagrant-up%60-hangs-at-%22Waiting-for-VM-to-boot.-This-can-take-a-few-minutes%22).
