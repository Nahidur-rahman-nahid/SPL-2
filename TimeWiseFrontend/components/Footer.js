const Footer = () => {
    return (
      <footer className="w-full py-8 bg-gray-800 text-gray-200 mt-16">
        <div className="container mx-auto px-4">
          <div className="flex flex-wrap justify-between">
            {/* About Section */}
            <div className="w-full md:w-1/4 mb-6 md:mb-0">
              <h3 className="text-lg font-bold mb-2">About Us</h3>
              <p className="text-sm">
                Medicare is a comprehensive online healthcare service platform designed to connect patients with doctors, hospitals, and medical services. Our mission is to provide seamless access to healthcare for everyone.
              </p>
            </div>
  
            {/* Quick Links Section */}
            <div className="w-full md:w-1/4 mb-6 md:mb-0">
              <h3 className="text-lg font-bold mb-2">Quick Links</h3>
              <ul className="text-sm">
                <li><a href="/" className="hover:text-blue-400 transition duration-300">Home</a></li>
                <li><a href="/about" className="hover:text-blue-400 transition duration-300">About</a></li>
                <li><a href="/contact" className="hover:text-blue-400 transition duration-300">Contact</a></li>
                <li><a href="/get-admitted" className="hover:text-blue-400 transition duration-300">Get Admitted as Doctor</a></li>
                <li><a href="/medicine-shop" className="hover:text-blue-400 transition duration-300">Medicine Shop</a></li>
                <li><a href="/hospital" className="hover:text-blue-400 transition duration-300">Hospital</a></li>
              </ul>
            </div>
  
            {/* Contact Section */}
            <div className="w-full md:w-1/4 mb-6 md:mb-0">
              <h3 className="text-lg font-bold mb-2">Contact Us</h3>
              <ul className="text-sm">
                <li>Email: <a href="mailto:info@medicare.com" className="hover:text-blue-400 transition duration-300">info@medicare.com</a></li>
                <li>Phone: <a href="tel:+1234567890" className="hover:text-blue-400 transition duration-300">+1 (234) 567-890</a></li>
                <li>Address: 123 Health St, Wellness City, HC 12345</li>
              </ul>
            </div>
  
            {/* Social Media Section */}
            <div className="w-full md:w-1/4 mb-6 md:mb-0">
              <h3 className="text-lg font-bold mb-2">Follow Us</h3>
              <div className="flex space-x-4">
                <a href="#" className="text-gray-200 hover:text-blue-400 transition duration-300"><i className="fab fa-facebook-f"></i></a>
                <a href="#" className="text-gray-200 hover:text-blue-400 transition duration-300"><i className="fab fa-twitter"></i></a>
                <a href="#" className="text-gray-200 hover:text-blue-400 transition duration-300"><i className="fab fa-linkedin-in"></i></a>
                <a href="#" className="text-gray-200 hover:text-blue-400 transition duration-300"><i className="fab fa-instagram"></i></a>
              </div>
            </div>
          </div>
  
          {/* Bottom Footer */}
          <div className="text-center mt-8 text-sm">
            &copy; {new Date().getFullYear()} Medicare. All rights reserved.
          </div>
        </div>
      </footer>
    );
  };
  
  export default Footer;
  