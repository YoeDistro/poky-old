.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

************************
Yocto Project Compatible
************************

============
Introduction
============

After the introduction of layers to OpenEmbedded, it quickly became clear that some layers were popular and worked well, others developed a reputation for being 'problematic'. Those were layers which didn't inter-operate well with other layers and tended to assume they controlled all the aspects of the end resulting output. This isn't usually intentional but because they're often developed by developers with a particular focus (e.g. a company's BSP) whilst the end users have a different focus (e.g. integrating that BSP into a product).

As a result of noticing patterns like this and friction between layers, the project developed the "Yocto Project Compatible" badge programme where layers modelling the best known practises could be marked as being widely compatible with other layers. This takes the form of a set of yes/no binary answer questions where layers can declare if they have met the appropriate criteria. In the second version of the programme, a script was added to make validation easier and clearer, the script is called 'yocto-check-layer' and is available in OpenEmbedded-Core.

========
Benefits
========

The OpenEmbedded Layer model is powerful and flexible, it gives users the ultimate power to change pretty much any aspect of the system but as with most things, power comes with responsibility. The Yocto Project would like to see people able to mix and match BSPs with distro configs or software stacks and be able to merge these together such that the result inter-operates well together. Over time, the project has realised that there were things that work well in layers to allow them to operate together. There were also 'anti-patterns' which stopped layers working well together.

The intent of the compatibility programme is simple, if the layer passes the compatibility tests, it is considered “well behaved” and should operate and cooperate well with other compatible layers.

The benefits of compatibility apply from multiple different user and member perspectives. From a hardware perspective (a BSP layer), compatibility means the hardware can be used in many different products and use cases without impacting the software stacks being run with it. For a company developing a product, compatibility gives you a specification/standard you can require in a contract and then know it will have certain desired characteristics for interoperability. It also puts constraints on how invasive these code bases are into the rest of the system, meaning that multiple different separate hardware support layers can coexist (e.g. for multiple product lines from different manufacturers). This can also influence how easily those system components might be upgraded or maintained for security purposes by one or more parties during the lifecycle of a product through development and widespread use.

==================
Validating a layer
==================

The badges are available to members of the project or open source projects run on a non-commercial basis and are tried to the project member level as a member benefit but anyone can answer the questions and run the script.

The project encourages all layer maintainers to consider the questions and the output from the script against their layer as there are often unintentional consequences of the way some layers are constructed and the questions and script are designed to highlight commonly known issues which can often easily be solved leading to easier and wider layer use.

It is intended that over time, the tests will evolve as best known practices are identified and as new interoperability issues are identified which unnecessarily restrict layer interoperability. If anyone becomes aware of either issue, please do mention it to the project (in our tech calls, on the mailing list or to the TSC). The TSC is holds overall responsibility for the technical criteria used by the programme.

Layers are divided into three types:

* "BSP" or "hardware support" layers contain support for particular pieces of hardware. This would include kernel and boot loader configuration, any recipes needed for firmware/modules needed for the hardware. They would usually correspond to a MACHINE setting.

* "distro" layers defined as layers providing configuration options and settings such as a choice of init system, compiler/optimisation options or configuration and choices of software components. This would usually correspond to a DSITRO setting.

* "software" layers are usually recipes. A layer might target a particular graphical UI or software stack component.

Key best practises the programme tries to encourage:

* A layer should clearly show who maintains it, where change submissions and bug reports should be sent
* Where multiple types of functionality are present, the layer be internally subdivided into layers to separate these components as users would likely want to use only one of them and separability is a key best practise.
* Adding a layer to a build should not change that build without the user taking some additional step of configuration to active the layer (such as selecting a MACHINE, a DISTRO or a DISTRO_FEATURE)

The project does test the compatibility status of the core project layers on the project autobuilder.

The official form to submit compatibility requests with is at https://www.yoctoproject.org/ecosystem/branding/compatible-registration/. Successful applicants can display the appropraiate badge which would be provided to them on success of the application.

